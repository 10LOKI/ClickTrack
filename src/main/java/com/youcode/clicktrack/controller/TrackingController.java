package com.youcode.clicktrack.controller;

import com.youcode.clicktrack.dto.ClickResponse;
import com.youcode.clicktrack.service.LienService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RequestMapping("/t")
public class TrackingController
{
    private final LienService lienService;

    public TrackingController(LienService lienService)
    {
        this.lienService = lienService;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ClickResponse> compterClic(@PathVariable("id") Long id)
    {
        return ResponseEntity.ok(lienService.compterClic(id));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<String> lienIntrouvable(NoSuchElementException exception)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> identifiantInvalide(IllegalArgumentException exception)
    {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
