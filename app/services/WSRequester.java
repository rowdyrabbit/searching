package services;

import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * A wrapper class around the Play WS service - so that we can mock out the WS class for unit testing.
 */
@Named
@Singleton
public class WSRequester {

    private static final String USER_AGENT_HEADER = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36";


    public Promise<WSResponse> get(String url, String searchTerms) {
        return WS.url(url + searchTerms).setHeader("User-Agent", USER_AGENT_HEADER).get();
    }
}
