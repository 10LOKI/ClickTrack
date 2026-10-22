package com.youcode.clicktrack.repository;

import com.youcode.clicktrack.model.Lien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class LienRepositoryImpl implements LienRepository
{
    private final List<Lien> liens = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public List<Lien> findByPublisherId(Long publisherId)
    {
        synchronized (liens)
        {
            List<Lien> resultat = new ArrayList<>();
            for (Lien lien : liens)
            {
                if (publisherId != null && publisherId.equals(lien.getPublisherId()))
                {
                    resultat.add(lien);
                }
            }
            return resultat;
        }
    }

    @Override
    public Optional<Lien> findById(Long id)
    {
        synchronized (liens)
        {
            return liens.stream().filter(lien -> lien.getId().equals(id)).findFirst();
        }
    }

    @Override
    public Lien save(Lien lien)
    {
        synchronized (liens)
        {
            lien.setId(sequence.incrementAndGet());
            liens.add(lien);
            return lien;
        }
    }
}
