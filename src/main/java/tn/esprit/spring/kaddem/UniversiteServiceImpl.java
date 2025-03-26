package tn.esprit.spring.kaddem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UniversiteServiceImpl implements IUniversiteService{
@Autowired
    UniversiteRepository universiteRepository;
@Autowired
    DepartementRepository departementRepository;
    public UniversiteServiceImpl() {
        // TODO Auto-generated constructor stub


    }
    @Override
  public   List<Universite> retrieveAllUniversites(){
return (List<Universite>) universiteRepository.findAll();
    }
    @Override
 public    Universite addUniversite (Universite  u){
return  (universiteRepository.save(u));
    }
    @Override
    public Universite updateUniversite(Long id, Universite universite) {
        // Logique de mise à jour de l'université avec l'identifiant id
        // Assurez-vous de renvoyer l'objet Universite mis à jour
        return universiteRepository.save(universite);
    }
    @Override
  public Universite retrieveUniversite (Integer idUniversite){
Universite u = universiteRepository.findById(idUniversite).get();
return  u;
    }
    @Override
    public  void deleteUniversite(Integer idUniversite){
        universiteRepository.delete(retrieveUniversite(idUniversite));
    }
    @Override
    public void assignUniversiteToDepartement(Integer idUniversite, Integer idDepartement){
        Universite u= universiteRepository.findById(idUniversite).orElse(null);
        Departement d= departementRepository.findById(idDepartement).orElse(null);
        u.getDepartements().add(d);
        universiteRepository.save(u);
    }
    @Override
    public Set<Departement> retrieveDepartementsByUniversite(Integer idUniversite){
Universite u=universiteRepository.findById(idUniversite).orElse(null);
return u.getDepartements();
    }

    @Override

    public List<Departement> findDepartementsWithStudentCount(Integer idUniversite, int minimumStudents) {
        Universite universite = universiteRepository.findById(idUniversite).orElse(null);
        if (universite == null) {
            return Collections.emptyList();
        }

        return universite.getDepartements().stream()
                .filter(departement -> departement.getEtudiants().size() >= minimumStudents)
                .collect(Collectors.toList());
    }


}
