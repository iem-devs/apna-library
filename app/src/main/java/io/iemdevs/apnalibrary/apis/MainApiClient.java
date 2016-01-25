package io.iemdevs.apnalibrary.apis;

import java.util.List;

import io.iemdevs.apnalibrary.models.books.BookModel;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Interface to implement GET, POST, PUT etc methods to call the API
 * Exact url endpoints are defined in this file
 */
public interface MainApiClient {
    @GET("/books")
    void getAllBooks(Callback<List<BookModel>> booksModelCallback);
}
