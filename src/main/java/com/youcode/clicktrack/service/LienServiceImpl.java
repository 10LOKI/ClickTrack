package com.youcode.clicktrack.service;

import com.youcode.clicktrack.dto.CreerLienRequest;
import com.youcode.clicktrack.dto.LienResponse;
import com.youcode.clicktrack.dto.OffreResponse;
import com.youcode.clicktrack.dto.ClickResponse;
import com.youcode.clicktrack.model.Publisher;
import com.youcode.clicktrack.model.Lien;
import com.youcode.clicktrack.repository.LienRepository;
import com.youcode.clicktrack.repository.PublisherRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class LienServiceImpl implements LienService
{
    private final LienRepository lienRepository;
    private final PublisherRepository publisherRepository;

    public LienServiceImpl(LienRepository lienRepository, PublisherRepository publisherRepository)
    {
        this.lienRepository = lienRepository;
        this.publisherRepository = publisherRepository;
    }

    @Override
    public ClickResponse compterClic(Long lienId)
    {
        if (lienId == null || lienId <= 0)
        {
            throw new IllegalArgumentException("L'identifiant du lien doit etre positif.");
        }
        Lien lien = lienRepository.findById(lienId)
                .orElseThrow(() -> new NoSuchElementException("Lien introuvable."));
        Publisher publisher = publisherRepository.findById(lien.getPublisherId())
                .orElseThrow(() -> new NoSuchElementException("Publisher introuvable."));

        int clicks = lien.incrementerClicks();
        return new ClickResponse(lien.getUrlDestination(),
                publisher.getPrenom() + " " + publisher.getNom(), lien.getOffre(), clicks);
    }

    @Override
    public List<OffreResponse> listerOffres(Long publisherId)
    {
        if (publisherId == null || publisherId <= 0)
        {
            throw new IllegalArgumentException("L'identifiant du publisher doit etre positif.");
        }
        if (publisherRepository.findById(publisherId).isEmpty())
        {
            throw new NoSuchElementException("Publisher introuvable.");
        }

        Map<String, OffreResponse> offres = new LinkedHashMap<>();
        for (Lien lien : lienRepository.findByPublisherId(publisherId))
        {
            OffreResponse offre = offres.get(lien.getOffre());
            if (offre == null)
            {
                offre = new OffreResponse(lien.getOffre());
                offres.put(lien.getOffre(), offre);
            }
            offre.setNombreLiens(offre.getNombreLiens() + 1);
            offre.setClicks(offre.getClicks() + lien.getClicks());
        }
        return new ArrayList<>(offres.values());
    }

    @Override
    public List<LienResponse> listerLiens(Long publisherId)
    {
        if (publisherId == null || publisherId <= 0)
        {
            throw new IllegalArgumentException("L'identifiant du publisher doit etre positif.");
        }
        if (publisherRepository.findById(publisherId).isEmpty())
        {
            throw new NoSuchElementException("Publisher introuvable.");
        }

        List<LienResponse> resultat = new ArrayList<>();
        for (Lien lien : lienRepository.findByPublisherId(publisherId))
        {
            resultat.add(new LienResponse(lien.getId(), lien.getOffre(),
                    lien.getUrlDestination(), lien.getClicks(), lien.getPublisherId()));
        }
        return resultat;
    }

    @Override
    public LienResponse creerLien(Long publisherId, CreerLienRequest request)
    {
        if (publisherId == null || publisherId <= 0)
        {
            throw new IllegalArgumentException("L'identifiant du publisher doit etre positif.");
        }
        if (publisherRepository.findById(publisherId).isEmpty())
        {
            throw new NoSuchElementException("Publisher introuvable.");
        }
        if (request == null || request.getOffre() == null || request.getOffre().isBlank())
        {
            throw new IllegalArgumentException("L'offre est obligatoire.");
        }
        if (request.getUrlDestination() == null || request.getUrlDestination().isBlank())
        {
            throw new IllegalArgumentException("L'URL de destination est obligatoire.");
        }

        String urlDestination = request.getUrlDestination().trim();
        try
        {
            URI uri = new URI(urlDestination);
            boolean httpOuHttps = "http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme());
            if (!httpOuHttps || uri.getHost() == null)
            {
                throw new IllegalArgumentException("L'URL doit etre une adresse HTTP ou HTTPS avec un domaine.");
            }
        }
        catch (URISyntaxException exception)
        {
            throw new IllegalArgumentException("L'URL de destination est invalide.", exception);
        }

        Lien lien = new Lien(null, request.getOffre().trim(), urlDestination, publisherId);
        Lien lienEnregistre = lienRepository.save(lien);
        return new LienResponse(lienEnregistre.getId(), lienEnregistre.getOffre(),
                lienEnregistre.getUrlDestination(), lienEnregistre.getClicks(), lienEnregistre.getPublisherId());
    }
}
