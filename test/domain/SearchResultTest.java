package domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchResultTest {


    @Test
    public void shouldCreateSearchResult() {
        final SearchResult result = new SearchResult("Twitter", "https://www.twitter.com/");
        assertEquals("https://www.twitter.com/", result.getUrl());
        assertEquals("Twitter", result.getTitle());
    }

    @Test
    public void shouldHaveExpectedToString() {
        final SearchResult result = new SearchResult("Twitter", "https://www.twitter.com/");
        assertEquals("Twitter - https://www.twitter.com/", result.toString());
    }

}
