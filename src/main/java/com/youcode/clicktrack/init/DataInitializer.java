package com.youcode.clicktrack.init;

import com.youcode.clicktrack.model.Categorie;
import com.youcode.clicktrack.model.Publisher;
import com.youcode.clicktrack.repository.PublisherRepository;

public class DataInitializer
{
    private final PublisherRepository publisherRepository;

    public DataInitializer(PublisherRepository publisherRepository)
    {
        this.publisherRepository = publisherRepository;
    }

    public void initialiser()
    {
        publisherRepository.save(new Publisher(null, "Martin", "Alice",
                "alice@example.com", Categorie.INFLUENCEUR));
        publisherRepository.save(new Publisher(null, "Bernard", "Yanis",
                "yanis@example.com", Categorie.YOUTUBEUR));
        publisherRepository.save(new Publisher(null, "Petit", "Sara",
                "sara@example.com", Categorie.BLOGUEUR));
    }
}
