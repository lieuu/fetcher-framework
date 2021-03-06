package com.lieuu.fetcher;

import com.lieuu.fetcher.exception.FetcherErrorCallback;
import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.response.FetcherResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class WaterfallCachingFetcher<T> implements MultiFetcher<T> {

  private final static FetcherErrorCallback DEFAULT_ERROR_CALLBACK = e -> e.printStackTrace();

  private final int fetchersSize;
  private final FetcherErrorCallback errorCallback;
  private final List<CachingFetcher<T>> fetchers;

  @SafeVarargs
  public WaterfallCachingFetcher(final CachingFetcher<T>... fetchers) {
    this(WaterfallCachingFetcher.DEFAULT_ERROR_CALLBACK, fetchers);
  }

  @SafeVarargs
  public WaterfallCachingFetcher(final FetcherErrorCallback errorCallback,
    final CachingFetcher<T>... fetchers) {
    this(errorCallback, Arrays.asList(fetchers));
  }

  public WaterfallCachingFetcher(final List<CachingFetcher<T>> fetchers) {
    this(WaterfallCachingFetcher.DEFAULT_ERROR_CALLBACK, fetchers);
  }

  public WaterfallCachingFetcher(final FetcherErrorCallback errorCallback,
    final List<CachingFetcher<T>> fetchers) {
    this.fetchers = Collections.unmodifiableList(fetchers);
    this.errorCallback = errorCallback;
    this.fetchersSize = fetchers.size();
  }

  @Override
  public FetcherResponse<T> fetch() throws FetcherException {

    if (this.fetchersSize == 0) {
      throw new FetcherException("Number of fetchers was zero!");
    }

    for (int i = 0; i < (this.fetchersSize - 1); i++) {

      final CachingFetcher<T> fetcher = this.fetchers.get(i);

      try {
        return FetcherResponseFactory.getFetcherResponse(i + 1, fetcher.fetch());
      }
      catch (final FetcherException e) {
        this.errorCallback.onError(e);
      }

    }

    return FetcherResponseFactory.getFetcherResponse(this.fetchers.size(),
      this.fetchers.get(this.fetchersSize - 1).fetch());

  }

}
