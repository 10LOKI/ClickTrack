package com.youcode.clicktrack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youcode.clicktrack.model.Lien;
import com.youcode.clicktrack.repository.LienRepository;
import com.youcode.clicktrack.repository.PublisherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.XmlWebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PublisherControllerTest
{
    private ClassPathXmlApplicationContext root;
    private XmlWebApplicationContext web;
    private MockMvc mvc;
    private LienRepository liens;
    private Long publisherId;

    @BeforeEach
    void preparer()
    {
        root = new ClassPathXmlApplicationContext("applicationContext.xml");
        PublisherRepository publishers = root.getBean(PublisherRepository.class);
        publisherId = publishers.findAll().get(0).getId();
        liens = root.getBean(LienRepository.class);

        web = new XmlWebApplicationContext();
        web.setParent(root);
        web.setServletContext(new MockServletContext("src/main/webapp", new FileSystemResourceLoader()));
        web.setConfigLocation("/WEB-INF/dispatcher-servlet.xml");
        web.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @AfterEach
    void fermer()
    {
        if (web != null) web.close();
        if (root != null) root.close();
    }

    @Test
    void creerLienRetourne201EtEnregistreLesDonnees() throws Exception
    {
        String body = mvc.perform(post("/api/publishers/{id}/liens", publisherId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"offre\":\"Formation Java\",\"urlDestination\":\"https://example.com/java\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        JsonNode json = new ObjectMapper().readTree(body);
        assertEquals("Formation Java", json.get("offre").asText());
        assertEquals("https://example.com/java", json.get("urlDestination").asText());
        assertEquals(publisherId.longValue(), json.get("publisherId").asLong());
        assertEquals(0, json.get("clicks").asInt());
        assertTrue(liens.findById(json.get("id").asLong()).isPresent());
        assertEquals(1, liens.findByPublisherId(publisherId).size());
    }

    @Test
    void publisherAbsentRetourne404() throws Exception
    {
        mvc.perform(post("/api/publishers/999/liens")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"offre\":\"Java\",\"urlDestination\":\"https://example.com\"}"))
                .andExpect(status().isNotFound());
        assertTrue(liens.findByPublisherId(999L).isEmpty());
    }

    @Test
    void donneesInvalidesRetournenT400SansInsertion() throws Exception
    {
        String[] bodies = {
                "{\"offre\":\" \",\"urlDestination\":\"https://example.com\"}",
                "{\"offre\":\"Java\",\"urlDestination\":\"ftp://example.com\"}",
                "{}", "{invalide", ""
        };
        for (String body : bodies)
        {
            mvc.perform(post("/api/publishers/{id}/liens", publisherId)
                    .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        assertTrue(liens.findByPublisherId(publisherId).isEmpty());
    }

    @Test
    void identifiantsInvalidesRetournent400() throws Exception
    {
        for (String id : new String[]{"0", "abc"})
        {
            mvc.perform(post("/api/publishers/{id}/liens", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"offre\":\"Java\",\"urlDestination\":\"https://example.com\"}"))
                    .andExpect(status().isBadRequest());
        }
        assertTrue(liens.findByPublisherId(publisherId).isEmpty());
    }

    @Test
    void listerLiensRetourneUniquementCeuxDuPublisherSansCompterDeClic() throws Exception
    {
        Lien premier = liens.save(new Lien(null, "Java", "https://example.com/java", publisherId));
        premier.incrementerClicks();
        premier.incrementerClicks();
        liens.save(new Lien(null, "Spring", "https://example.com/spring", publisherId));
        liens.save(new Lien(null, "Autre publisher", "https://example.org", 2L));

        String body = mvc.perform(get("/api/publishers/{id}/liens", publisherId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode json = new ObjectMapper().readTree(body);
        assertTrue(json.isArray());
        assertEquals(2, json.size());
        assertEquals(premier.getId().longValue(), json.get(0).get("id").asLong());
        assertEquals("Java", json.get(0).get("offre").asText());
        assertEquals("https://example.com/java", json.get(0).get("urlDestination").asText());
        assertEquals(2, json.get(0).get("clicks").asInt());
        assertEquals(0, json.get(1).get("clicks").asInt());
        for (JsonNode lien : json)
        {
            assertEquals(publisherId.longValue(), lien.get("publisherId").asLong());
        }
        assertEquals(2, premier.getClicks());
    }

    @Test
    void listerLiensRetourneUneListeVidePourUnPublisherSansLien() throws Exception
    {
        String body = mvc.perform(get("/api/publishers/{id}/liens", publisherId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode json = new ObjectMapper().readTree(body);
        assertTrue(json.isArray());
        assertEquals(0, json.size());
    }

    @Test
    void listerLiensVerifieLePublisher() throws Exception
    {
        mvc.perform(get("/api/publishers/999/liens")).andExpect(status().isNotFound());
        for (String id : new String[]{"0", "-1", "abc"})
        {
            mvc.perform(get("/api/publishers/{id}/liens", id)).andExpect(status().isBadRequest());
        }
    }

    @Test
    void methodeEtTypeDeContenuNonSupportes() throws Exception
    {
        mvc.perform(put("/api/publishers/{id}/liens", publisherId))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/api/publishers/{id}/liens", publisherId)
                .contentType(MediaType.TEXT_PLAIN).content("Java"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
