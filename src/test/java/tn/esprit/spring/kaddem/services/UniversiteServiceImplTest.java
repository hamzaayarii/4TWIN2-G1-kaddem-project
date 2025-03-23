package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UniversiteServiceImplTest {

    @Autowired
    private IUniversiteService universiteService;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Test
    void testRetrieveAllUniversites() {
        // Given: Ajouter des universités dans la base
        Universite u1 = new Universite();
        u1.setNomUniv("ESPRIT");
        universiteRepository.save(u1);

        Universite u2 = new Universite();
        u2.setNomUniv("INSAT");
        universiteRepository.save(u2);

        // When: On récupère toutes les universités
        List<Universite> universites = universiteService.retrieveAllUniversites();

        // Then: Vérifier que la liste contient au moins 2 universités
        assertTrue(universites.size() >= 2);
    }
}
