/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package com.lieuu.fetcher;

import com.lieuu.fetcher.response.FetcherResponse;
import com.lieuu.fetcher.response.source.Source;

class FetcherResponseFactory<S> {

  protected static <S> FetcherResponse<S> getFetcherResponse(final int rankFinal, final S value) {

    return new FetcherResponse<S>() {

      @Override
      public Source source() {
        return Source.valueOf(rankFinal);
      }

      @Override
      public S value() {
        return value;
      }

    };

  }

}
