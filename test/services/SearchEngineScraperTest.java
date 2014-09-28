package services;

import domain.SearchResult;
import org.junit.Before;
import org.junit.Test;
import play.libs.F.Promise;
import play.libs.F.Function0;
import play.libs.ws.WSResponse;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

public class SearchEngineScraperTest {

    private static final String GOOGLE_RESP = "<div id=\"search\"><div id=\"ires\"><ol><li class=\"g\"><h3 class=\"r\"><a href=\"http://www.cat.com/\">Caterpillar: <b>Cat</b> | global-selector</a></h3></li></ol></div></div>";

    private final WSRequester wsRequester = mock(WSRequester.class);
    private final SearchEngineScraper scraper = new SearchEngineScraper(wsRequester);


    @Before
    public void setup() {
        Promise<WSResponse> googleResp = Promise.promise(new Function0<WSResponse>() {
            public WSResponse apply() {
                final WSResponse googleResponse = mock(WSResponse.class);
                when(googleResponse.getBody()).thenReturn(GOOGLE_RESP);
                return googleResponse;
            }
        });
        when(wsRequester.get(anyString(), eq(SearchEngineScraper.SearchEngine.GOOGLE.getUrl()))).thenReturn(googleResp);
    }

    @Test
    public void shouldReturnExpectedSearchResult() {
        final Promise<List<SearchResult>> promise = scraper.search("cat", SearchEngineScraper.SearchEngine.GOOGLE);

        final List<SearchResult> results = promise.get(10000);
        assertEquals(1, results.size());
        final SearchResult result = results.get(0);
        assertEquals("Caterpillar: Cat | global-selector", result.getTitle());
        assertEquals("http://www.cat.com/", result.getUrl());
    }



}
