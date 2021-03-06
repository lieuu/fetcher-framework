package com.lieuu.fetcher;

import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.response.FetcherResponse;

import java.util.Arrays;
import java.util.List;

class ExpiringMultiConcurrentFetcher<T> extends BlockingMultiConcurrentFetcher<T> {

  private volatile long lastClearTime;
  private final double maxCacheTimeMs;

  @SafeVarargs
  public ExpiringMultiConcurrentFetcher(final int maxCacheTimeMs,
    final NonBlockingConcurrentFetcher<T>... fetchers) {
    this((double) maxCacheTimeMs, fetchers);
  }

  @SafeVarargs
  public ExpiringMultiConcurrentFetcher(final double maxCacheTimeMs,
    final NonBlockingConcurrentFetcher<T>... fetchers) {
    this(maxCacheTimeMs, Arrays.asList(fetchers));
  }

  public ExpiringMultiConcurrentFetcher(final double maxCacheTimeMs,
    final List<NonBlockingConcurrentFetcher<T>> fetchers) {
    super(fetchers);
    this.maxCacheTimeMs = maxCacheTimeMs;
    this.lastClearTime = System.currentTimeMillis();
  }

  @Override
  public final synchronized FetcherResponse<T> fetch() throws FetcherException {

    if ((System.currentTimeMillis() - this.lastClearTime) >= this.maxCacheTimeMs) {
      this.clearFuture();
      this.lastClearTime = System.currentTimeMillis();
    }

    return super.fetch();

  }

}
