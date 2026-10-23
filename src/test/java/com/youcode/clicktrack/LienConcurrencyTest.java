package com.youcode.clicktrack;

import com.youcode.clicktrack.model.Lien;
import com.youcode.clicktrack.repository.LienRepository;
import com.youcode.clicktrack.service.LienService;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LienConcurrencyTest
{
    @Test
    void clicsSimultanesNePerdentAucuneIncrementation() throws Exception
    {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml"))
        {
            LienRepository repository = context.getBean(LienRepository.class);
            LienService service = context.getBean(LienService.class);
            Lien lien = repository.save(new Lien(null, "Java", "https://example.com/java", 1L));
            ExecutorService pool = Executors.newFixedThreadPool(8);
            CountDownLatch depart = new CountDownLatch(1);
            List<Future<List<Integer>>> resultats = new ArrayList<>();
            try
            {
                for (int thread = 0; thread < 8; thread++)
                {
                    resultats.add(pool.submit(() -> {
                        depart.await();
                        List<Integer> compteurs = new ArrayList<>();
                        for (int clic = 0; clic < 250; clic++)
                        {
                            compteurs.add(service.compterClic(lien.getId()).getClicks());
                        }
                        return compteurs;
                    }));
                }
                depart.countDown();
                Set<Integer> compteurs = new HashSet<>();
                for (Future<List<Integer>> resultat : resultats)
                {
                    compteurs.addAll(resultat.get(10, TimeUnit.SECONDS));
                }
                assertEquals(2000, lien.getClicks());
                assertEquals(2000, compteurs.size());
                for (int attendu = 1; attendu <= 2000; attendu++)
                {
                    org.junit.jupiter.api.Assertions.assertTrue(compteurs.contains(attendu));
                }
            }
            finally
            {
                pool.shutdownNow();
            }
        }
    }
}
