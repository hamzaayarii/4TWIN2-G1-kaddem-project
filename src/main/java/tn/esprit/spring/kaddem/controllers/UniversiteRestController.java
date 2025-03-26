package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.UniversiteServiceImpl;
import tn.esprit.spring.kaddem.dto.UniversiteDTO;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.IUniversiteService;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/universite")
public class UniversiteRestController {

	@Autowired
	private UniversiteServiceImpl universiteService;
	private final UniversiteRepository universiteRepository;

	@Autowired
	public UniversiteRestController(UniversiteServiceImpl universiteService, UniversiteRepository universiteRepository) {
		this.universiteService = universiteService;
		this.universiteRepository = universiteRepository;
	}

	// http://localhost:8089/Kaddem/universite/retrieve-all-universites
	@GetMapping("/retrieve-all-universites")
	@ResponseBody
	public List<Universite> getUniversites() {
		return universiteService.retrieveAllUniversites();
	}

	// http://localhost:8089/Kaddem/universite/retrieve-universite/8
	@GetMapping("/universites/{id}")
	public ResponseEntity<Universite> retrieveUniversite(@PathVariable int id) {
		Universite universite = universiteService.retrieveUniversite(id);
		return ResponseEntity.ok(universite); // Utilisez le statut approprié ici
	}


	// http://localhost:8089/Kaddem/universite/add-universite
	@PostMapping
	public ResponseEntity<Universite> addUniversite(@RequestBody UniversiteDTO universiteDTO) {
		Universite universite = convertDTOToUniversite(universiteDTO);

		Universite addedUniversite = universiteService.addUniversite(universite);

		return new ResponseEntity<>(addedUniversite, HttpStatus.CREATED);
	}

	private Universite convertDTOToUniversite(UniversiteDTO universiteDTO) {
		Universite universite = new Universite();
		universite.setNomUniv(universiteDTO.getNomUniv());
		// Ajoutez d'autres champs en fonction de votre modèle Universite

		return universite;
	}

	@PutMapping("/{id}")
	public ResponseEntity<Universite> updateUniversite(@PathVariable Long id, @RequestBody UniversiteDTO universiteDTO) {
		// Logique de mise à jour de l'université avec l'identifiant id
		Universite universite = convertDTOToUniversite(universiteDTO);
		Universite updatedUniversite = universiteService.updateUniversite(id, universite);
		return ResponseEntity.ok(updatedUniversite);
	}

	@DeleteMapping("/delete-universite/{universite-id}")
	public ResponseEntity<Void> deleteUniversite(@PathVariable("universite-id") Integer universiteId) {
		try {
			universiteService.deleteUniversite(universiteId);
			return ResponseEntity.ok().build(); // 200 OK
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
		}
	}

	@PutMapping(value="/affecter-universite-departement/{universiteId}/{departementId}")
	public void affectertUniversiteToDepartement(@PathVariable("universiteId") Integer universiteId, @PathVariable("departementId")Integer departementId){
		universiteService.assignUniversiteToDepartement(universiteId, departementId);
	}


}
