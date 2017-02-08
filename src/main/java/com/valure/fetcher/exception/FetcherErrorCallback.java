package com.valure.fetcher.exception;

public interface FetcherErrorCallback {

    /**
     * Called when waterfallFetcher intermediary fetcher fails. This is not a
     * fatal error and should not be treated as such.
     *
     * @param e
     */
    public void onError(Throwable e);

}
