package tn.esprit.spring.kaddem.services;

import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;

import java.util.List;
import java.util.Set;

public interface IUniversiteService {
   public List<Universite> retrieveAllUniversites();

    Universite addUniversite (Universite  u);

    Universite updateUniversite (Universite  u);

    Universite retrieveUniversite (Integer idUniversite);

    public  void deleteUniversite(Integer idUniversite);

    public void assignUniversiteToDepartement(Integer idUniversite, Integer idDepartement);

    public Set<Departement> retrieveDepartementsByUniversite(Integer idUniversite);

 public List<Departement> findDepartementsWithStudentCount(Integer idUniversite, int minimumStudents);

/* public Map<String, Integer> getStatistiquesEtudiantsParDepartement(Integer idUniversite);
 public double getTauxRemplissageUniversite(Integer idUniversite);
 public List<Universite> findUniversitesByAccreditationAndCapacite(boolean accreditation, int capaciteMinimum);*/
}
