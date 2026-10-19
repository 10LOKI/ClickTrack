package com.youcode.clicktrack.repository;

import com.youcode.clicktrack.model.Publisher;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PublisherRepositoryImpl implements PublisherRepository
{
    private final List<Publisher> publishers = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public List<Publisher> findAll()
    {
        synchronized (publishers)
        {
            return (new ArrayList<>(publishers));
        }
    }

    @Override
    public Optional<Publisher> findById(Long id)
    {
        synchronized (publishers)
        {
            return (publishers.stream().filter(p -> p.getId().equals(id)).findFirst());
        }
    }

    @Override
    public Publisher save(Publisher publisher)
    {
        publisher.setId(sequence.incrementAndGet());
        publishers.add(publisher);
        return (publisher);
    }
}