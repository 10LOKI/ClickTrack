package com.youcode.clicktrack.controller;

import com.youcode.clicktrack.dto.CreerLienRequest;
import com.youcode.clicktrack.dto.LienResponse;
import com.youcode.clicktrack.service.LienService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RequestMapping("/api/publishers")
public class PublisherController
{
    private final LienService lienService;

    public PublisherController(LienService lienService)
    {
        this.lienService = lienService;
    }

    @PostMapping("/{id}/liens")
    @ResponseBody
    public ResponseEntity<LienResponse> creerLien(
            @PathVariable("id") Long id, @RequestBody CreerLienRequest request)
    {
        LienResponse response = lienService.creerLien(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> donneesInvalides(IllegalArgumentException exception)
    {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<String> publisherIntrouvable(NoSuchElementException exception)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
