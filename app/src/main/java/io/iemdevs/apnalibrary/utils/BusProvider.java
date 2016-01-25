package io.iemdevs.apnalibrary.utils;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * BusProvider singleton class.
 * Returns Otto Bus instance which is used for app-wide communication
 */
public class BusProvider {
    private static BusProvider instance;
    private Bus bus;

    public static void initInstance() {
        if(instance == null) {
            instance = new BusProvider();
        }
    }

    public static BusProvider getInstance() {
        if(instance == null) {
            initInstance();
        }
        return instance;
    }

    private BusProvider() {
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public Bus getBus() {
        return bus;
    }
}
