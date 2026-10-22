package com.youcode.clicktrack.dto;

public class CreerLienRequest
{
    private String offre;
    private String urlDestination;

    public CreerLienRequest() {}

    public String getOffre() { return offre; }
    public void setOffre(String offre) { this.offre = offre; }

    public String getUrlDestination() { return urlDestination; }
    public void setUrlDestination(String urlDestination) { this.urlDestination = urlDestination; }
}
