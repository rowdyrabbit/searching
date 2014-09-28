package controllers;

import java.util.ArrayList;
import java.util.List;

import domain.SearchResult;
import org.junit.*;

import play.mvc.*;
import play.libs.F.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import org.junit.Test;

import services.SearchEngineScraper;



public class ApplicationTest {

    private final SearchEngineScraper scraper = mock(SearchEngineScraper.class);
    private final Application application = new Application(scraper);

    @Before
    public void setup() {
        Promise scrapeResult = Promise.promise(new Function0<List<SearchResult>>() {
                                                   public List<SearchResult> apply() {
                                                       List<SearchResult> results = new ArrayList();
                                                       results.add(new SearchResult("cats", "www.cat.com"));
                                                       results.add(new SearchResult("dogs", "www.dog.com"));
                                                       return results;
                                                   }
                                               });
        when(scraper.scrape(anyString())).thenReturn(scrapeResult);
    }

    @Test
    public void testSearchResult() throws Exception {
        Promise<Result> promise = application.search("cat");
        Result result = promise.get(10000);
        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentAsString(result)).isEqualTo("[{\"title\":\"cats\",\"url\":\"www.cat.com\"},{\"title\":\"dogs\",\"url\":\"www.dog.com\"}]");
    }

    @Test
    public void testThatSearchTermsAreURLEncoded() throws Exception {
        application.search("fail cat");
        verify(scraper, times(1)).scrape("fail+cat");
    }

}
