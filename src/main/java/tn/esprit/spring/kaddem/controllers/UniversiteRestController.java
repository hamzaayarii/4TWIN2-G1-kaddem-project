package tn.esprit.spring.kaddem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.UniversiteServiceImpl;
import tn.esprit.spring.kaddem.entities.Universite;

import java.util.List;

@RestController
@RequestMapping("/universite")
public class UniversiteRestController {

	@Autowired
	private UniversiteServiceImpl universiteService;

	// GET all
	@GetMapping("/retrieve-all-universites")
	public List<Universite> getUniversites() {
		return universiteService.retrieveAllUniversites();
	}

	// GET by ID
	@GetMapping("/universites/{id}")
	public ResponseEntity<Universite> retrieveUniversite(@PathVariable int id) {
		Universite universite = universiteService.retrieveUniversite(id);
		if (universite != null) {
			return ResponseEntity.ok(universite);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// POST
	@PostMapping
	public ResponseEntity<Universite> addUniversite(@RequestBody Universite universite) {
		try {
			Universite addedUniversite = universiteService.addUniversite(universite);
			return new ResponseEntity<>(addedUniversite, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace(); // This will print the actual cause in the console
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}


	// PUT
	@PutMapping("/{id}")
	public ResponseEntity<Universite> updateUniversite(@PathVariable Long id, @RequestBody Universite universite) {
		Universite updatedUniversite = universiteService.updateUniversite(id, universite);
		return ResponseEntity.ok(updatedUniversite);
	}

	// DELETE
	@DeleteMapping("/delete-universite/{universite-id}")
	public ResponseEntity<Void> deleteUniversite(@PathVariable("universite-id") Integer universiteId) {
		try {
			universiteService.deleteUniversite(universiteId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
