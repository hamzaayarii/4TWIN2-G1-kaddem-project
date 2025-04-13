package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    @Test
    void testRetrieveAllEtudiants() {
        Etudiant e1 = new Etudiant("Ali", "Ben Ali");
        Etudiant e2 = new Etudiant("Samira", "Ben Mohamed");
        when(etudiantRepository.findAll()).thenReturn(Arrays.asList(e1, e2));
        
        List<Etudiant> etudiants = etudiantService.retrieveAllEtudiants();

        assertEquals(2, etudiants.size());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void testAddEtudiant() {
        Etudiant e = new Etudiant("Doe", "Nouh");
        when(etudiantRepository.save(e)).thenReturn(e);

        Etudiant savedEtudiant = etudiantService.addEtudiant(e);

        assertNotNull(savedEtudiant);
        assertEquals("Nouh", savedEtudiant.getPrenomE()); 
        assertEquals("Doe", savedEtudiant.getNomE());      
        verify(etudiantRepository, times(1)).save(e);
    }

    @Test
    void testRemoveEtudiant() {
        Integer id = 1;
        Etudiant e = new Etudiant("Test", "Delete");
        e.setIdEtudiant(id);
        
        when(etudiantRepository.findById(id)).thenReturn(Optional.of(e));
        doNothing().when(etudiantRepository).delete(e);

        assertDoesNotThrow(() -> etudiantService.removeEtudiant(id));
        verify(etudiantRepository, times(1)).delete(e);
    }

    @Test
    void testUpdateEtudiant() {
        Etudiant e = new Etudiant("Old", "Name");
        e.setIdEtudiant(1);
        e.setNomE("New");
        e.setPrenomE("Name");
        
        when(etudiantRepository.save(e)).thenReturn(e);
        
        Etudiant updatedEtudiant = etudiantService.updateEtudiant(e);
        
        assertNotNull(updatedEtudiant);
        assertEquals("New", updatedEtudiant.getNomE());
        assertEquals("Name", updatedEtudiant.getPrenomE());
        verify(etudiantRepository, times(1)).save(e);
    }

    @Test
    void testRetrieveEtudiant() {
        Integer id = 1;
        Etudiant e = new Etudiant("Test", "Student");
        e.setIdEtudiant(id);
        
        when(etudiantRepository.findById(id)).thenReturn(Optional.of(e));
        
        Etudiant found = etudiantService.retrieveEtudiant(id);
        
        assertNotNull(found);
        assertEquals(id, found.getIdEtudiant());
        verify(etudiantRepository, times(1)).findById(id);
    }

    @Test
    void testAssignEtudiantToDepartement() {
        Integer etudiantId = 1;
        Integer departementId = 1;
        
        Etudiant etudiant = new Etudiant("Test", "Student");
        Departement departement = new Departement();
        departement.setIdDepart(departementId);
        
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.of(etudiant));
        when(departementRepository.findById(departementId)).thenReturn(Optional.of(departement));
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);
        
        etudiantService.assignEtudiantToDepartement(etudiantId, departementId);
        
        verify(etudiantRepository).findById(etudiantId);
        verify(departementRepository).findById(departementId);
        verify(etudiantRepository).save(etudiant);
        assertEquals(departement, etudiant.getDepartement());
    }

    @Test
    void testAddAndAssignEtudiantToEquipeAndContract() {
        Integer contratId = 1;
        Integer equipeId = 1;
        Etudiant etudiant = new Etudiant("Test", "Student");
        Contrat contrat = new Contrat();
        Equipe equipe = new Equipe();
        equipe.setEtudiants(new HashSet<>());
        
        when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));
        when(equipeRepository.findById(equipeId)).thenReturn(Optional.of(equipe));
        
        Etudiant result = etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, contratId, equipeId);
        
        assertNotNull(result);
        assertEquals(etudiant, contrat.getEtudiant());
        assertTrue(equipe.getEtudiants().contains(etudiant));
        verify(contratRepository).findById(contratId);
        verify(equipeRepository).findById(equipeId);
    }

    @Test
    void testGetEtudiantsByDepartement() {
        Integer departementId = 1;
        List<Etudiant> expectedStudents = Arrays.asList(
            new Etudiant("Student1", "Test1"),
            new Etudiant("Student2", "Test2")
        );
        
        when(etudiantRepository.findEtudiantsByDepartement_IdDepart(departementId))
            .thenReturn(expectedStudents);
        
        List<Etudiant> result = etudiantService.getEtudiantsByDepartement(departementId);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(etudiantRepository).findEtudiantsByDepartement_IdDepart(departementId);
    }

    @Test
    void testRetrieveEtudiantNotFound() {
        Integer id = 999;
        when(etudiantRepository.findById(id)).thenReturn(Optional.empty());
        
        assertThrows(NoSuchElementException.class, () -> {
            etudiantService.retrieveEtudiant(id);
        });
    }

    @Test
    void testAssignEtudiantToDepartementWithInvalidIds() {
        Integer etudiantId = 999;
        Integer departementId = 999;
        
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.empty());
        when(departementRepository.findById(departementId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            etudiantService.assignEtudiantToDepartement(etudiantId, departementId);
        });
    }
}
