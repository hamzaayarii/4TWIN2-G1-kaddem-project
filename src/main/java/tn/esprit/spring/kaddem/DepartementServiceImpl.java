package tn.esprit.spring.kaddem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import java.util.List;

@Slf4j
@Service
public class DepartementServiceImpl implements IDepartementService{
	@Autowired
	DepartementRepository departementRepository;
	public List<Departement> retrieveAllDepartements(){
		log.info("Début de récupération de tous les départements");
		return (List<Departement>) departementRepository.findAll();
	}

	public Departement addDepartement (Departement d){
		log.info("Ajout d'un nouvel département : {}", d.getNomDepart());
		Departement saved_d = departementRepository.save(d);
		log.info("Département ajouté avec l'ID : {}", saved_d.getIdDepart());
		return saved_d;
	}

	public   Departement updateDepartement (Departement d){
		log.warn("Mise à jour du département ID : {}", d.getIdDepart());
		return departementRepository.save(d);
	}

	public  Departement retrieveDepartement (Integer idDepart){
		log.info("Récupération du département ID : {}", idDepart);
		return departementRepository.findById(idDepart).get();
	}
	public  void deleteDepartement(Integer idDepartement){
		log.error("Suppression du département ID : {}", idDepartement);
		Departement d=retrieveDepartement(idDepartement);
		departementRepository.delete(d);
	}



}
