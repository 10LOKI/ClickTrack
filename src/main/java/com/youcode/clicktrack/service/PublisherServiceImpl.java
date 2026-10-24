package com.youcode.clicktrack.service;

import com.youcode.clicktrack.model.Publisher;
import com.youcode.clicktrack.repository.PublisherRepository;
import java.util.List;

public class PublisherServiceImpl implements PublisherService
{
    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository)
    {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public List<Publisher> listerPublishers()
    {
        return publisherRepository.findAll();
    }
}
