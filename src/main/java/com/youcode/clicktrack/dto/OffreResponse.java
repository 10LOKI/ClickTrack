package com.youcode.clicktrack.dto;

public class OffreResponse
{
    private final String offre;
    private int nombreLiens;
    private long clicks;

    public OffreResponse(String offre)
    {
        this.offre = offre;
    }

    public String getOffre() { return offre; }
    public int getNombreLiens() { return nombreLiens; }
    public void setNombreLiens(int nombreLiens) { this.nombreLiens = nombreLiens; }
    public long getClicks() { return clicks; }
    public void setClicks(long clicks) { this.clicks = clicks; }
}
