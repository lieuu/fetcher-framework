package com.lieuu.fetcher.caching.multi;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.exception.FetcherException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

public class TripleWaterfallCachingFetcherTest {

  @Test
  public void tripleWaterfallCachingFetcherTest() {

    final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
      e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
      (() -> "test"), (() -> {
        throw new FetcherException("should never reach here!");
      }), (() -> {
        throw new FetcherException("should never reach here!");
      }));

    try {
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
    }
    catch (final FetcherException e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void tripleWaterfallCachingFetcherBackupTest() {

    final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
      e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"), (() -> {
        throw new FetcherException("should never reach here!");
      }), (() -> "test"), (() -> {
        throw new FetcherException("should never reach here!");
      }));

    try {
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
    }
    catch (final FetcherException e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void tripleWaterfallCachingFetcherBackupBackupTest() {

    final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
      e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"), (() -> {
        throw new FetcherException("should never reach here!");
      }), (() -> {
        throw new FetcherException("should never reach here!");
      }), (() -> "test"));

    try {
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
      Assert.assertEquals("test", fetcher.fetch());
    }
    catch (final FetcherException e) {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void tripleWaterfallCachingFetcherAllFailingTest() {

    final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
      e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"), (() -> {
        throw new FetcherException("should never reach here!");
      }), (() -> {
        throw new FetcherException("should never reach here!");
      }), (() -> {
        throw new FetcherException("should reach here!");
      }));

    try {
      fetcher.fetch();
      Assert.fail(); // should not reach this
    }
    catch (final FetcherException e) {
      Assertions.assertThat(e.getMessage()).contains("should reach here!");
    }

  }

}
