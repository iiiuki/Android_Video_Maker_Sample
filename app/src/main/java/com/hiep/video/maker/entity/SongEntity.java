package com.hiep.video.maker.entity;

/**
 * Created by Hiep on 7/15/2016.
 */
public class SongEntity {
    private long id;
    private String title;
    private String Artist;
    private String path;

    public SongEntity(long id, String title, String artist, String path) {
        this.id = id;
        this.title = title;
        Artist = artist;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
