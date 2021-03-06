package com.openclassrooms.go4launch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed GHERBAL (pour OC) on 14/01/2022
 */
public class NearByAPIResponse {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = new ArrayList<Object>();

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;

    // No List Result
    @SerializedName("result")
    @Expose
    private Result result;

    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<Result>();

    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The htmlAttributions
     */
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    /**
     *
     * @param htmlAttributions
     * The html_attributions
     */
    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    /**
     *
     * @return
     * The nextPageToken
     */
    public String getNextPageToken() {
        return nextPageToken;
    }

    /**
     *
     * @param nextPageToken
     * The next_page_token
     */
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
