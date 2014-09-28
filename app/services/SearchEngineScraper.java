package services;


import domain.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.Logger;
import play.libs.F.Promise;
import play.libs.F.Function;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains functionality for connecting to each search engine and parsing the response to extract search results.
 */
@Named
@Singleton
public class SearchEngineScraper {

    private final WSRequester requester;

    @Inject
    public SearchEngineScraper(final WSRequester requester) {
        this.requester = requester;
    }

    /**
     * Represents the config info for connecting to each engine and scraping results.
     */
    protected enum SearchEngine {
        GOOGLE("http://www.google.com/search?output=search&q=", "#search li.g h3.r"),
        YAHOO("http://search.yahoo.com/search?p=", "ol li div.res div h3 a"),
        BING("https://www.bing.com/search?q=", "#b_results li.b_algo h2 a");

        private String selector;
        private String url;
        SearchEngine(String url, String selector) {
            this.url = url;
            this.selector = selector;
        }

        public String getUrl() {
            return url;
        }
    }

    /**
     * @param searchTerms the search terms to invoke each search engine with
     * @return a list of combined search results.
     */
    public Promise<List<SearchResult>> scrape(String searchTerms) {

        final Promise<List<SearchResult>> googleSearchResults = search(searchTerms, SearchEngine.GOOGLE);
        final Promise<List<SearchResult>> bingSearchResults = search(searchTerms, SearchEngine.BING);
        final Promise<List<SearchResult>> yahooSearchResults = search(searchTerms, SearchEngine.YAHOO);

        //convert these results to JSON and add to result.
        Promise<List<List<SearchResult>>> combinedPromise = Promise.sequence(googleSearchResults, bingSearchResults, yahooSearchResults);

        return combinedPromise.map(new Function<List<List<SearchResult>>, List<SearchResult>>() {
            public List<SearchResult> apply(List<List<SearchResult>> results) {
                List<SearchResult> flattened = new ArrayList();
                for (List<SearchResult> r : results) {
                    flattened.addAll(r);
                }
                Logger.debug("Found a total of " + flattened.size() + " search results.");
                return flattened;
            }
        });
    }

    /**
     * @param searchTerms - the search terms to look up
     * @param engine - the search engine to scrape results from
     * @return - a list of matching search results from this particular engine
     */
    protected Promise<List<SearchResult>> search(final String searchTerms, final SearchEngine engine) {
        Promise<WSResponse> response = requester.get(engine.url, searchTerms);
        Promise<List<SearchResult>> result = response.map(
                new Function<WSResponse, List<SearchResult>>() {
                    public List<SearchResult> apply(WSResponse response) {
                        Document doc = Jsoup.parse(response.getBody());
                        Elements results = doc.select(engine.selector);
                        final List<SearchResult> searchResults = buildResultList(results);
                        Logger.debug("Found " + searchResults.size() + " for search terms: " + searchTerms + " in search engine: " + engine.name());
                        return searchResults;
                    }
                }
        );
        return result;
    }

    /**
     * Parses the matching Elements and converts them to SearchResult objects
     */
    private List<SearchResult> buildResultList(Elements results) {
        List<SearchResult> parsedResults = new ArrayList();
        for (Element result : results) {
            SearchResult sr = new SearchResult(result.select("a").text(), result.select("a").attr("href"));
            parsedResults.add(sr);
        }
        return parsedResults;
    }
}
