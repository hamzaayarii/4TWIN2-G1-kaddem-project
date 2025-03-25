package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.services.IUniversiteService;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/universite")
public class UniversiteRestController {
	@Autowired
	IUniversiteService universiteService;
	// http://localhost:8089/kaddem/universite/retrieve-all-universites
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/retrieve-all-universites")
	public List<Universite> getUniversites() {
		List<Universite> listUniversites = universiteService.retrieveAllUniversites();
		return listUniversites;
	}
	// http://localhost:8089/kaddem/universite/retrieve-universite/8
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/retrieve-universite/{universite-id}")
	public Universite retrieveUniversite(@PathVariable("universite-id") Integer universiteId) {
		return universiteService.retrieveUniversite(universiteId);
	}

	// http://localhost:8089/kaddem/universite/add-universite
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/add-universite")
	public Universite addUniversite(@RequestBody Universite u) {
		Universite universite = universiteService.addUniversite(u);
		return universite;
	}

	// http://localhost:8089/Kaddem/universite/remove-universite/1
	@CrossOrigin(origins = "http://localhost:4200")
	@DeleteMapping("/remove-universite/{universite-id}")
	public void removeUniversite(@PathVariable("universite-id") Integer universiteId) {
		universiteService.deleteUniversite(universiteId);
	}

	// http://localhost:8089/Kaddem/universite/update-universite
	@CrossOrigin(origins = "http://localhost:4200")
	@PutMapping("/update-universite")
	public Universite updateUniversite(@RequestBody Universite u) {
		Universite u1= universiteService.updateUniversite(u);
		return u1;
	}

	//@PutMapping("/affecter-etudiant-departement")
	@CrossOrigin(origins = "http://localhost:4200")
	@PutMapping(value="/affecter-universite-departement/{universiteId}/{departementId}")
	public void affectertUniversiteToDepartement(@PathVariable("universiteId") Integer universiteId, @PathVariable("departementId")Integer departementId){
		universiteService.assignUniversiteToDepartement(universiteId, departementId);
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping(value = "/listerDepartementsUniversite/{idUniversite}")
	public Set<Departement> listerDepartementsUniversite(@PathVariable("idUniversite") Integer idUniversite) {

		return universiteService.retrieveDepartementsByUniversite(idUniversite);
	}

}


