package tn.esprit.spring.kaddem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartementServiceTest {

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private DepartementServiceImpl departementService;


    @Test
    void retrieveAllDepartements_ShouldReturnListOfDepartements() {
        Departement d1 = new Departement(1, "TWIN");
        Departement d2 = new Departement(2, "NIDS");
        when(departementRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<Departement> result = departementService.retrieveAllDepartements();

        assertEquals(2, result.size());
        verify(departementRepository, times(1)).findAll();
    }

    @Test
    void addDepartement_ShouldSaveAndReturnDepartement() {
        Departement departement = new Departement(1, "ARCTIC");
        when(departementRepository.save(departement)).thenReturn(departement);

        Departement result = departementService.addDepartement(departement);

        assertNotNull(result);
        assertEquals("ARCTIC", result.getNomDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void updateDepartement_ShouldUpdateAndReturnDepartement() {
        Departement departement = new Departement(1, "BI");
        when(departementRepository.save(departement)).thenReturn(departement);

        Departement result = departementService.updateDepartement(departement);

        assertEquals("BI", result.getNomDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void retrieveDepartement_ShouldReturnDepartementById() {
        Departement departement = new Departement(1, "TWIN");
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        Departement result = departementService.retrieveDepartement(1);

        assertNotNull(result);
        assertEquals(1, result.getIdDepart());
        verify(departementRepository, times(1)).findById(1);
    }

    @Test
    void retrieveDepartement_ShouldThrowExceptionWhenNotFound() {
        when(departementRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> departementService.retrieveDepartement(99));
    }

    @Test
    void deleteDepartement_ShouldDeleteExistingDepartement() {
        Departement departement = new Departement(1, "TWIN");
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        doNothing().when(departementRepository).delete(departement);

        departementService.deleteDepartement(1);

        verify(departementRepository, times(1)).delete(departement);
    }
}
