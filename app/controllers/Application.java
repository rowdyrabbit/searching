package controllers;

import domain.SearchResult;

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


    public Promise<Result> search(String searchTerms) throws UnsupportedEncodingException {
        // url encode the search term
        final String encodedSearchTerms =  URLEncoder.encode(searchTerms, "utf-8");

        //convert these results to JSON and add to result.
        final Promise<List<SearchResult>> results = scraper.scrape(encodedSearchTerms);

        return results.map(new Function<List<SearchResult>, Result>() {
            public Result apply(List<SearchResult> results) {
                return ok(Json.toJson(results));
            }
        });
    }
}
