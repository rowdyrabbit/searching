package domain;


/**
 * Domain object to represent search results from each search engine
 */
public class SearchResult {

    private String title;
    private String url;

    /**
     *
     * @param title the title of the search result
     * @param url the search result URL
     */
    public SearchResult(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return title + " - " + url;
    }
}
