package tn.esprit.spring.kaddem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UniversiteServiceImpl implements IUniversiteService {

    private final UniversiteRepository universiteRepository;

    @Autowired
    public UniversiteServiceImpl(UniversiteRepository universiteRepository) {
        this.universiteRepository = universiteRepository;
    }

   @Override
public List<Universite> retrieveAllUniversites() {
    return (List<Universite>) universiteRepository.findAll();
}
    public Universite addUniversite(Universite u) {
        return universiteRepository.save(u);
    }

    @Override
    public Universite updateUniversite(Long id, Universite universite) {
        // Logique de mise à jour de l'université avec l'identifiant id
        // Assurez-vous de renvoyer l'objet Universite mis à jour
        return universiteRepository.save(universite);
    }


    public Universite retrieveUniversite(Integer idUniversite) {
        Optional<Universite> optionalUniversite = universiteRepository.findById(idUniversite);

        if (optionalUniversite.isPresent()) {
            return optionalUniversite.get();
        } else {
            // Handle the case where the university is not found
            return null; // or throw an appropriate exception
        }
    }

    public void deleteUniversite(Integer idUniversite) {
        universiteRepository.delete(retrieveUniversite(idUniversite));
    }
}