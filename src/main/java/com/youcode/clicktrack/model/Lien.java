package com.youcode.clicktrack.model;

import java.util.concurrent.atomic.AtomicInteger;

public class Lien
{
    private Long    id;
    private String  offre;
    private String  urlDestination;
    private final   AtomicInteger clicks = new AtomicInteger(0);
    private Long    publisherId;

    public Lien() {
    }

    public Lien(Long id, String offre, String urlDestination, Long publisherId) {
        this.id = id;
        this.offre = offre;
        this.urlDestination = urlDestination;
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

    public String getUrlDestination() { return urlDestination; }
    public void setUrlDestination(String urlDestination) { this.urlDestination = urlDestination; }

    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
}
