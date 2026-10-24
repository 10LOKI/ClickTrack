package com.youcode.clicktrack.repository;

import com.youcode.clicktrack.model.Lien;

import java.util.List;
import java.util.Optional;

public interface LienRepository
{
    List<Lien> findByPublisherId(Long publisherId);
    Optional<Lien> findById(Long id);
    Lien save(Lien lien);
}
