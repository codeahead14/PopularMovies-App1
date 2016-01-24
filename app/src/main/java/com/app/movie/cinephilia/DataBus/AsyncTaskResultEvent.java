package com.app.movie.cinephilia.DataBus;

/**
 * Created by GAURAV on 23-01-2016.
 */
public class AsyncTaskResultEvent {
    private boolean result;

    public AsyncTaskResultEvent(boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }
}
