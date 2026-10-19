package com.youcode.clicktrack.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Lien
{
    private Long    id;
    private String  offre;
    private String  urlDest;
    private final   AtomicInteger clicks = new AtomicInteger(0);
    private Long    publisherId;

    public Lien() {
    }

    public Lien(Long id, String offre, String urlDest, Long publisherId) {
        this.id = id;
        this.offre = offre;
        this.urlDest = urlDest;
        this.publisherId = publisherId;
    }

    public int incrementerClicks() {
        return clicks.incrementAndGet();
    }

    public int getClicks() { return clicks.get(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOffre() { return offre; }
    public void setOffre(String offre) { this.offre = offre; }

    public String getUrlDest() { return urlDest; }
    public void setUrlDest(String urlDest) { this.urlDest = urlDest; }

    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
}