import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.services.EtudiantServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    @Test
    public void testRetrieveAllEtudiants() {
        Etudiant e1 = new Etudiant("Ali", "Ben Ali");
        Etudiant e2 = new Etudiant("Samira", "Ben Mohamed");
        when(etudiantRepository.findAll()).thenReturn(Arrays.asList(e1, e2));
        
        List<Etudiant> etudiants = etudiantService.retrieveAllEtudiants();

        assertEquals(2, etudiants.size());
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
public void testAddEtudiant() {
    Etudiant e = new Etudiant("Doe", "Nouh");
    when(etudiantRepository.save(e)).thenReturn(e);

    Etudiant savedEtudiant = etudiantService.addEtudiant(e);

    assertNotNull(savedEtudiant);
    assertEquals("Nouh", savedEtudiant.getPrenomE()); 
    assertEquals("Doe", savedEtudiant.getNomE());      
    verify(etudiantRepository, times(1)).save(e);
}

    @Test
    public void testRemoveEtudiant() {
        Integer id = 1;
        Etudiant e = new Etudiant("Test", "Delete");
        e.setIdEtudiant(id);
        
        when(etudiantRepository.findById(id)).thenReturn(Optional.of(e));
        doNothing().when(etudiantRepository).delete(e);

        assertDoesNotThrow(() -> etudiantService.removeEtudiant(id));
        verify(etudiantRepository, times(1)).delete(e);
    }
}
