/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package com.rentworthy.fetcher;

import com.rentworthy.fetcher.exception.FetcherException;

class MultiFetcherValue<T> implements Fetcher<T> {

    private final MultiFetcher<T> fetcher;

    /**
     * @param fetcher
     */
    public MultiFetcherValue(final MultiFetcher<T> fetcher) {
        this.fetcher = fetcher;
    }

    /*
     * (non-Javadoc)
     * @see com.rentworthy.fetcher.Fetcher#fetch()
     */
    @Override
    public T fetch() throws FetcherException {
        return this.fetcher.fetch().value();
    }

}