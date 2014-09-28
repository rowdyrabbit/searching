package controllers;

import domain.SearchResult;

import play.Logger;
import play.libs.Json;
import play.mvc.*;
import play.libs.F.Function;
import play.libs.F.Promise;
import services.SearchEngineScraper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Named
@Singleton
public class Application extends Controller {

    private final SearchEngineScraper scraper;

    @Inject
    public Application(final SearchEngineScraper scraper) {
        this.scraper = scraper;
    }

    /**
     * @param searchTerms - the search terms as passed into the API
     * @return a JSON response with the search results from all search engines
     * @throws UnsupportedEncodingException - will only be thrown if UTF-8 encoding cannot be found on the deployment server.
     */
    public Promise<Result> search(String searchTerms) throws UnsupportedEncodingException {
        try {
            // url encode the search terms
            final String encodedSearchTerms =  URLEncoder.encode(searchTerms, "utf-8");
            // scrape the search engines for results
            final Promise<List<SearchResult>> results = scraper.scrape(encodedSearchTerms);
            // convert the list of results to JSON and send response
            return results.map(new Function<List<SearchResult>, Result>() {
                public Result apply(List<SearchResult> results) {
                    Logger.debug("Found " + results.size() + " search results.");
                    return ok(Json.toJson(results));
                }
            });
        } catch (UnsupportedEncodingException ex) {
            Logger.error("UTF-8 encoding not found on this system: " + ex.getMessage());
            throw ex;
        }
    }
}
