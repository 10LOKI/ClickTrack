package com.youcode.clicktrack.controller;

import com.youcode.clicktrack.dto.CreerLienRequest;
import com.youcode.clicktrack.dto.LienResponse;
import com.youcode.clicktrack.dto.OffreResponse;
import com.youcode.clicktrack.service.LienService;
import com.youcode.clicktrack.service.PublisherService;
import com.youcode.clicktrack.model.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.List;

@RequestMapping("/api/publishers")
public class PublisherController
{
    private final LienService lienService;
    private final PublisherService publisherService;

    public PublisherController(LienService lienService, PublisherService publisherService)
    {
        this.lienService = lienService;
        this.publisherService = publisherService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Publisher>> listerPublishers()
    {
        return ResponseEntity.ok(publisherService.listerPublishers());
    }

    @PostMapping("/{id}/liens")
    @ResponseBody
    public ResponseEntity<LienResponse> creerLien(
            @PathVariable("id") Long id, @RequestBody CreerLienRequest request)
    {
        LienResponse response = lienService.creerLien(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/liens")
    @ResponseBody
    public ResponseEntity<List<LienResponse>> listerLiens(@PathVariable("id") Long id)
    {
        return ResponseEntity.ok(lienService.listerLiens(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> donneesInvalides(IllegalArgumentException exception)
    {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @GetMapping("/{id}/offres")
    @ResponseBody
    public ResponseEntity<List<OffreResponse>> listerOffres(@PathVariable("id") Long id)
    {
        return ResponseEntity.ok(lienService.listerOffres(id));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<String> publisherIntrouvable(NoSuchElementException exception)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
