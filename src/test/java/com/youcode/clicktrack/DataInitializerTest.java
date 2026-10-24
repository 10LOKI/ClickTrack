package com.youcode.clicktrack;

import com.youcode.clicktrack.model.Categorie;
import com.youcode.clicktrack.model.Publisher;
import com.youcode.clicktrack.repository.LienRepository;
import com.youcode.clicktrack.repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataInitializerTest
{
    @Test
    void xmlInitialiseTroisPublishersSansCreerDeLiens()
    {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml"))
        {
            PublisherRepository repository = context.getBean(PublisherRepository.class);
            LienRepository liens = context.getBean(LienRepository.class);
            List<Publisher> publishers = repository.findAll();

            assertEquals(3, publishers.size());
            assertEquals("alice@example.com", repository.findById(1L).orElseThrow().getEmail());
            assertEquals(Categorie.INFLUENCEUR, publishers.get(0).getCategorie());
            assertEquals(Categorie.YOUTUBEUR, publishers.get(1).getCategorie());
            assertEquals(Categorie.BLOGUEUR, publishers.get(2).getCategorie());
            for (int i = 0; i < publishers.size(); i++)
            {
                assertEquals(Long.valueOf(i + 1), publishers.get(i).getId());
                assertTrue(liens.findByPublisherId(publishers.get(i).getId()).isEmpty());
            }
        }
    }
}
