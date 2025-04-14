package tn.esprit.spring.kaddem;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.spring.kaddem.controllers.UniversiteRestController;
import tn.esprit.spring.kaddem.dto.UniversiteDTO;
import tn.esprit.spring.kaddem.entities.Universite;

import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class UniversiteRestControllerTest {

    @Mock
    private UniversiteServiceImpl universiteService;

    @InjectMocks
    private UniversiteRestController universiteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // Autres initialisations si nécessaires
    }


    @Test
    void testGetAllUniversites() {
        // Mock data
        Universite universite1 = new Universite(1, "Universite1");
        Universite universite2 = new Universite(2, "Universite2");
        List<Universite> universiteList = Arrays.asList(universite1, universite2);

        // Mocking behavior
        when(universiteService.retrieveAllUniversites()).thenReturn(universiteList);

        // Perform the test
        List<Universite> result = universiteController.getUniversites();

        // Verify the interactions
        verify(universiteService, times(1)).retrieveAllUniversites();

        // Assertions
        assertEquals(2, result.size());
    }

    @Test
    void testGetUniversiteById() {
        // Mock data
        Universite universite = new Universite(1, "Universite1");

        // Mocking behavior
        when(universiteService.retrieveUniversite(1)).thenReturn(universite);

        // Perform the test
        ResponseEntity<Universite> responseEntity = universiteController.retrieveUniversite(1);

        // Verify the interactions
        verify(universiteService, times(1)).retrieveUniversite(1);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());  // Vérifiez le statut HTTP ici
        assertEquals(universite, responseEntity.getBody());
    }


    @Test
    void testAddUniversite() {
        // Mock data
        Universite universiteDTO = new Universite();
        universiteDTO.setNomUniv("Universite1");

        // Mocking behavior
        when(universiteService.addUniversite(any(Universite.class))).thenReturn(new Universite());

        // Perform the test
        ResponseEntity<Universite> responseEntity = universiteController.addUniversite(universiteDTO);

        // Verify the interactions
        verify(universiteService, times(1)).addUniversite(any(Universite.class));

        // Assertions
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }


    @Test
    void testUpdateUniversite() {
        // Mock data
        Universite universiteDTO = new Universite();
        universiteDTO.setNomUniv("Universite1");

        // Mocking behavior
        when(universiteService.updateUniversite(any(Long.class), any(Universite.class))).thenReturn(new Universite());

        // Perform the test
        ResponseEntity<Universite> responseEntity = universiteController.updateUniversite(1L, universiteDTO);

        // Verify the interactions
        verify(universiteService, times(1)).updateUniversite(any(Long.class), any(Universite.class));

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }


    @Test
    void testDeleteUniversite() {
        // Mock data
        Universite universite = new Universite(1, "Universite1");

        // Mocking behavior
        when(universiteService.retrieveUniversite(1)).thenReturn(universite);

        // Perform the test
        ResponseEntity<Void> responseEntity = universiteController.deleteUniversite(1);

        // Verify the interactions
        verify(universiteService, times(1)).deleteUniversite(1);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}