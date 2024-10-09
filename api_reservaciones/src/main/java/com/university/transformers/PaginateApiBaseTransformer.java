package com.university.transformers;

import org.springframework.http.HttpStatus;
import java.util.List;

public class PaginateApiBaseTransformer extends ApiBaseTransformer {

    private int total;
    private int lastPage;
    private int currentPage;
    private int perPage;
    private String nextPageUrl;
    private String prevPageUrl;

    public PaginateApiBaseTransformer() {
        super();
    }

    public PaginateApiBaseTransformer(HttpStatus status, String message, Object data, String warning, String error,
            List<String> errors, List<String> warnings) {
        super(status, message, data, warning, error, errors, warnings);
    }

    public PaginateApiBaseTransformer(HttpStatus status, String message, Object data, String warning, String error,
            List<String> errors, List<String> warnings, int total, int lastPage, int currentPage, int perPage,
            String nextPageUrl, String prevPageUrl) {
        super(status, message, data, warning, error, errors, warnings);
        this.total = total;
        this.lastPage = lastPage;
        this.currentPage = currentPage;
        this.perPage = perPage;
        this.nextPageUrl = nextPageUrl;
        this.prevPageUrl = prevPageUrl;
    }

    public PaginateApiBaseTransformer(HttpStatus status, String message, Object data, String warning, String error) {
        super(status, message, data, warning, error);
    }

    public PaginateApiBaseTransformer(HttpStatus status, String message, Object data, String warning, String error, int total,
            int lastPage, int currentPage, int perPage, String nextPageUrl, String prevPageUrl) {
        super(status, message, data, warning, error);
        this.total = total;
        this.lastPage = lastPage;
        this.currentPage = currentPage;
        this.perPage = perPage;
        this.nextPageUrl = nextPageUrl;
        this.prevPageUrl = prevPageUrl;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getPrevPageUrl() {
        return prevPageUrl;
    }

    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }
}
