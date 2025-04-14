package tn.esprit.spring.kaddem;

import tn.esprit.spring.kaddem.entities.Universite;
import java.util.List;

public interface IUniversiteService {
    List<Universite> retrieveAllUniversites();

    Universite addUniversite (Universite  u);


    Universite updateUniversite(Long id, Universite universite);

    Universite retrieveUniversite (Integer idUniversite);

    public  void deleteUniversite(Integer idUniversite);

}