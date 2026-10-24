package com.youcode.clicktrack.repository;

import com.youcode.clicktrack.model.Publisher;

import java.util.List;
import java.util.Optional;

public interface PublisherRepository
{
    List<Publisher> findAll();
    Optional<Publisher> findById(Long id);
    Publisher save(Publisher publisher);
}