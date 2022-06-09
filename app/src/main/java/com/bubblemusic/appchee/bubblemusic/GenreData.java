package com.bubblemusic.appchee.bubblemusic;

public class GenreData {
    private Long id;
    private String title;
    public GenreData(Long _id, String _title)
    {
        this.id=_id;
        this.title=_title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
