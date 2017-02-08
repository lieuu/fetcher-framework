package com.fetcher;

import com.fetcher.exception.FetcherException;

public class ExpiringConcurrentCachingFetcherWrapper<T> extends ConcurrentCachingFetcherWrapper<T> {

    private static final double DEFAULT_MAX_CACHE_TIME = Double.MAX_VALUE;
    private volatile long lastClearTime;
    private final double maxCacheTimeMs;

    public ExpiringConcurrentCachingFetcherWrapper(final ConcurrentFetcherWrapper<T> fetcher) {
        this(fetcher, ExpiringConcurrentCachingFetcherWrapper.DEFAULT_MAX_CACHE_TIME);
    }

    public ExpiringConcurrentCachingFetcherWrapper(final ConcurrentFetcherWrapper<T> fetcher, final int maxCacheTimeMs) {
        this(fetcher, (double) maxCacheTimeMs);
    }

    public ExpiringConcurrentCachingFetcherWrapper(final ConcurrentFetcherWrapper<T> fetcher, final double maxCacheTimeMs) {
        super(fetcher);
        this.lastClearTime = System.currentTimeMillis();
        this.maxCacheTimeMs = maxCacheTimeMs;
    }

    @Override
    public synchronized T fetch() throws FetcherException {

        if ((System.currentTimeMillis() - this.lastClearTime) >= this.maxCacheTimeMs) {
            this.clearCachedObject();
            this.concurrentFetcherWrapper.clearFuture();
            this.lastClearTime = System.currentTimeMillis();
        }

        return super.fetch();

    }

}