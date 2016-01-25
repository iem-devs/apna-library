package io.iemdevs.apnalibrary.apis;

import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.otto.Bus;

import java.io.IOException;

import io.iemdevs.apnalibrary.utils.Config;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * API Client to communicate with backend server
 * Uses Retrofit and OkHTTP for server communication, Otto Bus for app-wide communication
 * Also uses Facebook's Stetho Library to inspect network calls
 * To use Stetho, open Chrome and navigate to chrome://inspect
 */
public class ApiClient {

    // replace endpoint address with server ip on production
    public static final String URL_UAT = "http://localhost:3000/api";
    public static final String URL_PROD = "";
    private RestAdapter mRestAdapter;
    private MainApiClient apiClient;
    private static String access_token;
    private static ApiClient apiService;
    private static Bus apiBus;
    private MainApiClient api;
    private OkHttpClient httpClient;
    private static int statusCode;


    /**
     * ApiService singleton class initializer
     * Singleton classes cannot be instantiated as the constructor is private
     * The usage of a Singleton class to keep one instance throughout the app
     * This method is used to initialize such instance
     * @param bus Otto EventBus
     */
    public static void initInstance(Bus bus) {
        if(apiService == null) {
            apiService = new ApiClient();
            apiBus = bus;
        }
    }

    /**
     * Method to return instance of the class
     * @param bus Otto EventBus
     * @return instance
     */
    public static ApiClient getApiService(Bus bus) {
        if(apiService == null) {
            initInstance(bus);
        }
        return apiService;
    }

    /**
     * Private constructor to make class a singleton
     */
    private ApiClient() {
        api = this.getRestAdapter();
    }

    public MainApiClient getApi() {
        return api;
    }
    /**
     * Generic response interface to be used by all
     * Dummy generic Retrofit response stubs
     * @param <T>
     */
    public interface INetworkResponse<T> {
        void onSuccess(T data);

        void onError(Exception e);
    }

    public interface ErrorNetworkResponse<T>{
        void onSuccess(T data);

        void onError(RetrofitError error);
    }

    /**
     * Initializes the adapter and returns it
     * The REST Adapter is created with Retrofit methods defined in MainApiClient
     * Injects authorization headers into request
     * Inspects status codes from response
     * @return rest adapter
     */
    private MainApiClient getRestAdapter() {

        httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(new StethoInterceptor());
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public com.squareup.okhttp.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                com.squareup.okhttp.Response response = chain.proceed(request);
                statusCode = response.code();
                return response;
            }
        });

        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(URL_UAT)
                .setClient(new OkClient(httpClient))
                .setRequestInterceptor(authHeader())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return mRestAdapter.create(MainApiClient.class);
    }



    /**
     * After getting the access token from the backend manually set it in the
     * ApiClient.setAccess_token method
     * @return RequestInterceptor
     */
    private static RequestInterceptor authHeader() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if(access_token!=null) {
                    request.addHeader(Config.AUTH_TOKEN_KEY, access_token);
                }
            }
        };
    }

    /**
     * API to get the callback inorder to start the request
     * Usage :
     * Callback<ModelType> callback = ApiClient.getOttoCallback(Config.SOME_TAG)
     * ApiClient.getApiService(bus).getApi().getSomeDataApi(callback)
     * @param accessType String to define the return type. Should be defined in Config file. Should
     *                   be different in order to identify the return value
     * @param <T> generic type
     * @return Retrofit Callback
     */
    public static <T> Callback<T> getOttoCallback(final String accessType) {
        final Bus mBus = apiBus;
        Callback<T> callback = new Callback<T>() {
            @Override
            public void success(T t, Response response) {
                Log.i(Config.TAG, "" + response.getStatus());
                if(mBus != null) {
                    mBus.post(t);
                    mBus.post(new RetrofitSucessEvent(response, accessType));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(Config.TAG,error.toString());
                if(mBus != null) {
                    mBus.post(new RetrofitErrorEvent(error, accessType));
                }
            }
        };
        return callback;
    }

    /**
     * Externally set bus
     * @param bus Otto Bus
     */
    public static void setBus(Bus bus) {
        apiBus = bus;
    }

    /**
     * Retrofit success event
     * N.B Use class based events to catch the success results
     * Example :
     * @Subscribe public void onBookRetrieved(BookModel book) {
     *     // do something with book
     * }
     * will be called on success.
     */
    public static class RetrofitSucessEvent {
        public Response response;
        public String accessType;

        public RetrofitSucessEvent(Response response, String accessType) {
            this.response = response;
            this.accessType = accessType;
        }
    }

    /**
     * Retrofit Error Event
     * when any error occurs, this event returns the error, accessType, and the errorCode
     * Usage :
     * on any class, add a Otto Subscriber:
     * @Subscribe public void onNetworkError(RetrofitErrorEvent errorEvent) {
     *     switch(errorEvent.accessType) {
     *         case Config.SOME_TAG :
     *           // do something
     *     }
     * }
     */
    public static class RetrofitErrorEvent {
        public RetrofitError error;
        public String accessType;
        public int errorCode;

        public RetrofitErrorEvent(RetrofitError error, String accessType) {
            this.error = error;
            this.accessType = accessType;
            this.errorCode = statusCode;
        }
    }

    /**
     * Getter for access_token
     * @return
     */
    public static String getAccess_token() {
        return access_token;
    }

    /**
     * Setter for access_token
     * @param token
     */
    public static void setAccess_token(String token) {
        access_token = token;
    }

}
