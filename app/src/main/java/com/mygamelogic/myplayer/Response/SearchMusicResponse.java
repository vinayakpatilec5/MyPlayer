package com.mygamelogic.myplayer.Response;

import java.util.List;

/**
 * Created by admin on 13/07/18.
 */

public class SearchMusicResponse {
    private List<MusicResponseRow> results;
    private int resultCount;

    public List<MusicResponseRow> getResults() {
        return results;
    }

    public void setResults(List<MusicResponseRow> results) {
        this.results = results;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    @Override
    public String toString() {
        return "SearchMusicResponse{" +
                "results=" + results +
                ", resultCount=" + resultCount +
                '}';
    }
}
