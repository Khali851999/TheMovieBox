
package com.example.themoviebox.Model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trailer {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("results")
    @Expose
    private List<TrailerResult> results = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TrailerResult> getResults() {
        return results;
    }

    public void setResults(List<TrailerResult> trailerResults) {
        this.results = trailerResults;
    }

}
