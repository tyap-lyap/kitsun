package ru.pinkgoosik.kitsun.api.modrinth;

@SuppressWarnings("unused")
public class SearchRequest {

    public String query = "";
    public Index index = Index.RELEVANT;
    public int limit = 10;
    public int offset = 0;

    public static Builder builder() {
        return new Builder();
    }

    public String getUrl() {
        String url = ModrinthAPI.API_URL + "/search?query='%QUERY%'&index=%INDEX%&limit=%LIMIT%&offset=%OFFSET%";
        url = url.replaceAll("%QUERY%", this.query);
        url = url.replaceAll("%INDEX%", this.index.toString());
        url = url.replaceAll("%LIMIT%", Integer.toString(this.limit));
        url = url.replaceAll("%OFFSET%", Integer.toString(this.offset));
        return url;
    }

    public static class Builder {
        private final SearchRequest request = new SearchRequest();

        public Builder setQuery(String query) {
            this.request.query = query;
            return this;
        }

        public Builder setIndex(Index index) {
            this.request.index = index;
            return this;
        }

        public Builder setLimit(int limit) {
            this.request.limit = limit;
            return this;
        }

        public Builder setOffset(int offset) {
            this.request.offset = offset;
            return this;
        }

        public SearchRequest build() {
            return this.request;
        }
    }

    public enum Index {
        RELEVANT("downloads"),
        DOWNLOADS("downloads"),
        FOLLOWS("follows"),
        NEWEST("newest"),
        UPDATED("updated");

        public final String name;
        Index(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
