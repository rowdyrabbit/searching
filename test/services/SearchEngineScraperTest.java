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
    private static final String YAHOO_RESP ="<ol start=\"1\"><div id=\"weblabeling\">Web results</div><li id=\"yui_3_10_0_1_1411892949811_343\"><div class=\"res\" id=\"yui_3_10_0_1_1411892949811_342\"><div id=\"yui_3_10_0_1_1411892949811_341\"><h3 id=\"yui_3_10_0_1_1411892949811_340\"><a id=\"link-1\" class=\"yschttl spt\" href=\"http://en.wikipedia.org%2fwiki%2fCat/\"><b>Cat</b> - Wikipedia, the free encyclopedia</a></h3></div></div></li></ol>";
    private static final String BING_RESP = "<ol id=\"b_results\"><li class=\"b_ans\"><h2><a href=\"/images/search?q=cat&amp;qpvt=cat&amp;FORM=IGRE\" h=\"ID=SERP,5069.1\">Images of <strong>cat</strong></a></h2></li><li class=\"b_algo\">\n" +
            "<h2><a href=\"http://www.cat.com/\" h=\"ID=SERP,5123.1\"><strong>Cat</strong> | global-selector | <strong>Caterpillar</strong></a></h2></li></ol>";

    private final WSRequester wsRequester = mock(WSRequester.class);
    private final SearchEngineScraper scraper = new SearchEngineScraper(wsRequester);


    @Before
    public void setup() {
        Promise<WSResponse> googleResp = Promise.promise(new Function0<WSResponse>() {
            public WSResponse apply() {
                final WSResponse resp = mock(WSResponse.class);
                when(resp.getBody()).thenReturn(GOOGLE_RESP);
                return resp;
            }
        });
        Promise<WSResponse> bingResp = Promise.promise(new Function0<WSResponse>() {
            public WSResponse apply() {
                final WSResponse resp = mock(WSResponse.class);
                when(resp.getBody()).thenReturn(BING_RESP);
                return resp;
            }
        });
        Promise<WSResponse> yahooResp = Promise.promise(new Function0<WSResponse>() {
            public WSResponse apply() {
                final WSResponse resp = mock(WSResponse.class);
                when(resp.getBody()).thenReturn(YAHOO_RESP);
                return resp;
            }
        });
        when(wsRequester.get(eq(SearchEngineScraper.SearchEngine.GOOGLE.getUrl()), anyString())).thenReturn(googleResp);
        when(wsRequester.get(eq(SearchEngineScraper.SearchEngine.BING.getUrl()), anyString())).thenReturn(bingResp);
        when(wsRequester.get(eq(SearchEngineScraper.SearchEngine.YAHOO.getUrl()), anyString())).thenReturn(yahooResp);
    }

    @Test
    public void shouldReturnCombinedSearchResults() {
        final Promise<List<SearchResult>> promise = scraper.scrape("cat");
        final List<SearchResult> results = promise.get(10000);
        assertEquals(3, results.size());
    }

    @Test
    public void shouldReturnExpectedSearchResultForGoogle() {
        final Promise<List<SearchResult>> promise = scraper.search("cat", SearchEngineScraper.SearchEngine.GOOGLE);

        final List<SearchResult> results = promise.get(10000);
        assertEquals(1, results.size());
        final SearchResult result = results.get(0);
        assertEquals("Caterpillar: Cat | global-selector", result.getTitle());
        assertEquals("http://www.cat.com/", result.getUrl());
    }

    @Test
    public void shouldReturnExpectedSearchResultForBing() {
        final Promise<List<SearchResult>> promise = scraper.search("cat", SearchEngineScraper.SearchEngine.BING);

        final List<SearchResult> results = promise.get(10000);
        assertEquals(1, results.size());
        final SearchResult result = results.get(0);
        assertEquals("Cat | global-selector | Caterpillar", result.getTitle());
        assertEquals("http://www.cat.com/", result.getUrl());
    }

    @Test
    public void shouldReturnExpectedSearchResultForYahoo() {
        final Promise<List<SearchResult>> promise = scraper.search("cat", SearchEngineScraper.SearchEngine.YAHOO);

        final List<SearchResult> results = promise.get(10000);
        assertEquals(1, results.size());
        final SearchResult result = results.get(0);
        assertEquals("Cat - Wikipedia, the free encyclopedia", result.getTitle());
        assertEquals("http://en.wikipedia.org%2fwiki%2fCat/", result.getUrl());
    }



}
