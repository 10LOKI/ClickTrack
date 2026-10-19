package com.youcode.clicktrack.model;

public class Publisher
{
    private Long        id;
    private String      nom;
    private String      prenom;
    private String      email;
    private Categorie   categorie;

    public Publisher()
    {

    }
    public Publisher(Long id , String nom , String prenom , String email , Categorie categorie)
    {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.categorie = categorie;
    }

    public long getId()
    {
        return (id);
    }
    public void setId(Long id)
    {
        this.id = id;
    }

    public String getNom()
    {
        return (nom);
    }
    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getPrenom()
    {
        return (prenom);
    }
    public void setPrenom(String prenom)
    {
        this.prenom = prenom;
    }
    public String getEmail()
    {
        return (email);
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public Categorie getCategorie()
    {
        return (categorie);
    }
    public void setCategorie(Categorie categorie)
    {
        this.categorie = categorie;
    }
}