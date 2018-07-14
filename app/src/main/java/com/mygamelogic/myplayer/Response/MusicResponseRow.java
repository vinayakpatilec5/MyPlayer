package com.mygamelogic.myplayer.Response;

/**
 * Created by admin on 13/07/18.
 */

public class MusicResponseRow {
    private String artistName;
    private String artworkUrl100;
    private String longDescription;
    private String previewUrl;
    private String trackName;
    private String primaryGenreName;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public void setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getPrimaryGenreName() {
        return primaryGenreName;
    }

    public void setPrimaryGenreName(String primaryGenreName) {
        this.primaryGenreName = primaryGenreName;
    }

    @Override
    public String toString() {
        return "MusicResponseRow{" +
                "artistName='" + artistName + '\'' +
                ", artworkUrl100='" + artworkUrl100 + '\'' +
                ", longDescription='" + longDescription + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                ", trackName='" + trackName + '\'' +
                ", primaryGenreName='" + primaryGenreName + '\'' +
                '}';
    }
}
