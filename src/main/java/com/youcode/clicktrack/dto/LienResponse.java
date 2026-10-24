package com.youcode.clicktrack.dto;

public class LienResponse
{
    private final Long id;
    private final String offre;
    private final String urlDestination;
    private final int clicks;
    private final Long publisherId;

    public LienResponse(Long id, String offre, String urlDestination, int clicks, Long publisherId)
    {
        this.id = id;
        this.offre = offre;
        this.urlDestination = urlDestination;
        this.clicks = clicks;
        this.publisherId = publisherId;
    }

    public Long getId() { return id; }
    public String getOffre() { return offre; }
    public String getUrlDestination() { return urlDestination; }
    public int getClicks() { return clicks; }
    public Long getPublisherId() { return publisherId; }
}
