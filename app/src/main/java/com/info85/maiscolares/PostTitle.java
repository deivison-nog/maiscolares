package com.info85.maiscolares;

public class PostTitle {
    private String title;
    private String content;
    private String imageLink;
    private String local;

    public PostTitle(String title, String content, String imageLink, String local) {
        this.title = title;
        this.content = content;
        this.imageLink = imageLink;
        this.local = local;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getLocal() {
        return local;
    }

    public String getTitleAsString() {
        return title;
    }
}
