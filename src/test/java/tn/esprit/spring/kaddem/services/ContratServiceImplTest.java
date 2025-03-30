package tn.esprit.spring.kaddem.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContratServiceImplTest {

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private ContratServiceImpl contratService;

    private Contrat contrat;
    private Etudiant etudiant;

    @BeforeEach
    void setUp() {
        contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setSpecialite(Specialite.IA);
        contrat.setMontantContrat(1000);
        contrat.setArchive(false);
        contrat.setDateFinContrat(new Date());

        etudiant = new Etudiant();
        etudiant.setNomE("Doe");
        etudiant.setPrenomE("John");
        etudiant.setContrats(new HashSet<>());
    }

    @Test
    void testRetrieveAllContrats() {
        List<Contrat> contrats = Arrays.asList(contrat);
        when(contratRepository.findAll()).thenReturn(contrats);

        List<Contrat> result = contratService.retrieveAllContrats();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(contratRepository, times(1)).findAll();
    }

    @Test
    void testAddContrat() {
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);

        Contrat savedContrat = contratService.addContrat(contrat);

        assertNotNull(savedContrat);
        assertEquals(1000, savedContrat.getMontantContrat());
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void testUpdateContrat() {
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);

        Contrat updatedContrat = contratService.updateContrat(contrat);

        assertNotNull(updatedContrat);
        verify(contratRepository, times(1)).save(contrat);
    }

    @Test
    void testRetrieveContrat() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));

        Contrat foundContrat = contratService.retrieveContrat(1);

        assertNotNull(foundContrat);
        assertEquals(1, foundContrat.getIdContrat());
        verify(contratRepository, times(1)).findById(1);
    }

    @Test
    void testRemoveContrat() {
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        doNothing().when(contratRepository).delete(any(Contrat.class));

        contratService.removeContrat(1);

        verify(contratRepository, times(1)).delete(contrat);
    }

    @Test
    void testAffectContratToEtudiant_Success() {
        // Créez un étudiant et un contrat valides
        Etudiant etudiant = new Etudiant();
        etudiant.setNomE("Doe");
        etudiant.setPrenomE("John");

        Contrat contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setArchive(false); // Assurez-vous qu'il est actif

        // Configurez les mocks
        when(etudiantRepository.findByNomEAndPrenomE("Doe", "John")).thenReturn(etudiant);
        when(contratRepository.findByIdContrat(1)).thenReturn(contrat);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(invocation -> {
            Contrat c = invocation.getArgument(0);
            c.setEtudiant(etudiant); // Affecte l'étudiant au contrat
            return c;
        });

        // Appelez la méthode à tester
        Contrat result = contratService.affectContratToEtudiant(1, "Doe", "John");

        // Assertions
        assertNotNull(result);
        assertEquals(etudiant, result.getEtudiant());
        verify(contratRepository, times(1)).save(any(Contrat.class));  // Vérifie si save a été appelé une fois
    }


    @Test
    void testAffectContratToEtudiant_Failure_StudentNotFound() {
        when(etudiantRepository.findByNomEAndPrenomE("Doe", "John")).thenReturn(null);

        Contrat result = contratService.affectContratToEtudiant(1, "Doe", "John");

        assertNull(result);
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    void testAffectContratToEtudiant_Failure_ContratNotFound() {
        // Utilisation de stubbing lenient pour éviter l'exception
        lenient().when(contratRepository.findById(1)).thenReturn(Optional.empty()); // uniquement le contrat

        // Simulation de la méthode
        Contrat result = contratService.affectContratToEtudiant(1, "Doe", "John");

        // Assertion sur le résultat
        assertNull(result); // le contrat n'a pas été affecté car non trouvé
        verify(contratRepository, never()).save(any(Contrat.class)); // Aucune tentative de sauvegarde
    }


    @Test
    void testGetChiffreAffaireEntreDeuxDates() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + (30L * 24 * 60 * 60 * 1000));  // 30 jours après la startDate

        // Création d'un contrat avec une date de fin dans la période
        Contrat contrat = new Contrat();
        contrat.setMontantContrat(300);  // Montant du contrat
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setDateFinContrat(endDate);  // La date de fin du contrat est dans la période

        // Mocking la réponse du repository
        when(contratRepository.findAll()).thenReturn(Collections.singletonList(contrat));

        // Appel de la méthode pour calculer le chiffre d'affaires
        Float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Vérification que le chiffre d'affaires retourné est bien celui attendu
        assertEquals(300f, result, 0.001f);
    }

    @Test
    void testGetChiffreAffaireEntreDeuxDates_WithMultipleContrats() {
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + (30L * 24 * 60 * 60 * 1000)); // 30 jours après la startDate

        // Create first contrat
        Contrat contrat = new Contrat();
        contrat.setMontantContrat(300);  // Ensure montantContrat is 300
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        contrat.setDateFinContrat(endDate);  // Ensure this contract ends within the date range

        // Create second contrat
        Contrat contrat2 = new Contrat();
        contrat2.setMontantContrat(500);  // Ensure montantContrat is 500
        contrat2.setSpecialite(Specialite.IA);
        contrat2.setArchive(false);
        contrat2.setDateFinContrat(endDate);  // Ensure this contract ends within the date range

        // Mock the repository response
        List<Contrat> contrats = Arrays.asList(contrat, contrat2);
        when(contratRepository.findAll()).thenReturn(contrats);

        // Call the service method to calculate the revenue
        Float result = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);

        // Assert that the revenue is the sum of the two contracts (300 + 500 = 800)
        assertEquals(800f, result, 0.001f);
    }



    @Test
    void testAddContrat_ContractArchived() {
        // Simule un contrat archivé
        contrat.setArchive(true);

        // Lorsque addContrat est appelé avec un contrat archivé, une IllegalStateException doit être levée
        assertThrows(IllegalStateException.class, () -> contratService.addContrat(contrat));

        // Vérifiez que la méthode save() n'a jamais été appelée
        verify(contratRepository, never()).save(any(Contrat.class));
    }

}