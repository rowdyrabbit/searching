package domain;


public class SearchResult {

    private String title;
    private String url;

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
