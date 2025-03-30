package tn.esprit.spring.kaddem.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.*;

@Service
public class ContratServiceImpl implements IContratService {
	private static final Logger logger = LogManager.getLogger(ContratServiceImpl.class);

	@Autowired
	ContratRepository contratRepository;

	@Autowired
	EtudiantRepository etudiantRepository;

	@Override
	public List<Contrat> retrieveAllContrats() {
		logger.info("Retrieving all contracts");
		List<Contrat> contrats = (List<Contrat>) contratRepository.findAll();
		logger.debug("Retrieved {} contracts", contrats.size());
		return contrats;
	}

	@Override
	public Contrat updateContrat(Contrat ce) {
		logger.info("Updating contract with ID: {}", ce.getIdContrat());
		try {
			Contrat updatedContrat = contratRepository.save(ce);
			logger.debug("Contract updated successfully: {}", updatedContrat);
			return updatedContrat;
		} catch (Exception e) {
			logger.error("Failed to update contract with ID: {}. Error: {}", ce.getIdContrat(), e.getMessage());
			throw e;
		}
	}

	@Override
	public Contrat addContrat(Contrat ce) {
		logger.info("Adding new contract: {}", ce);

		// Vérification si le contrat est archivé
		if (ce.getArchive() != null && ce.getArchive()) {
			// Log et levée de l'exception si le contrat est archivé
			logger.warn("Attempted to add an archived contract: {}", ce);
			throw new IllegalStateException("Cannot add an archived contract");
		}

		try {
			// Sauvegarde du contrat si ce n'est pas archivé
			Contrat savedContrat = contratRepository.save(ce);
			logger.debug("Contract added successfully with ID: {}", savedContrat.getIdContrat());
			return savedContrat;
		} catch (Exception e) {
			logger.error("Failed to add contract: {}. Error: {}", ce, e.getMessage());
			throw e;
		}
	}


	@Override
	public Contrat retrieveContrat(Integer idContrat) {
		logger.info("Retrieving contract with ID: {}", idContrat);
		Contrat contrat = contratRepository.findById(idContrat).orElse(null);
		if (contrat != null) {
			logger.debug("Contract found: {}", contrat);
		} else {
			logger.warn("No contract found with ID: {}", idContrat);
		}
		return contrat;
	}

	@Override
	public void removeContrat(Integer idContrat) {
		logger.info("Removing contract with ID: {}", idContrat);
		Contrat c = retrieveContrat(idContrat);
		if (c != null) {
			try {
				contratRepository.delete(c);
				logger.debug("Contract with ID: {} removed successfully", idContrat);
			} catch (Exception e) {
				logger.error("Failed to remove contract with ID: {}. Error: {}", idContrat, e.getMessage());
				throw e;
			}
		} else {
			logger.warn("Cannot remove contract: No contract found with ID: {}", idContrat);
		}
	}

	@Override
	public Contrat affectContratToEtudiant(Integer idContrat, String nomE, String prenomE) {
		logger.info("Assigning contract ID: {} to student: {} {}", idContrat, nomE, prenomE);

		Etudiant e = etudiantRepository.findByNomEAndPrenomE(nomE, prenomE);
		if (e == null) {
			logger.warn("Student not found: {} {}", nomE, prenomE);
			return null; // Si l'étudiant n'est pas trouvé, retourner null
		}

		Contrat ce = contratRepository.findByIdContrat(idContrat);
		if (ce == null) {
			logger.warn("Contract not found with ID: {}", idContrat);
			return null; // Si le contrat n'est pas trouvé, retourner null
		}

		// Gestion des contrats actifs
		Set<Contrat> contrats = Optional.ofNullable(e.getContrats()).orElse(new HashSet<>());
		long nbContratssActifs = contrats.stream()
				.filter(contrat -> Boolean.FALSE.equals(contrat.getArchive()))
				.count();

		logger.debug("Student {} {} has {} active contracts", nomE, prenomE, nbContratssActifs);

		// Limite de 5 contrats actifs
		if (nbContratssActifs < 5) {
			ce.setEtudiant(e);
			contratRepository.save(ce);
			logger.info("Contract ID: {} assigned to student: {} {}", idContrat, nomE, prenomE);
		} else {
			logger.warn("Cannot assign contract ID: {} - Student {} {} has reached maximum active contracts (5)", idContrat, nomE, prenomE);
		}

		return ce;
	}


	@Override
	public Integer nbContratsValides(Date startDate, Date endDate) {
		logger.info("Calculating number of valid contracts between {} and {}", startDate, endDate);
		try {
			Integer count = contratRepository.getnbContratsValides(startDate, endDate);
			logger.debug("Found {} valid contracts", count);
			return count;
		} catch (Exception e) {
			logger.error("Error calculating valid contracts between {} and {}. Error: {}", startDate, endDate, e.getMessage());
			throw e;
		}
	}

	@Override
	public void retrieveAndUpdateStatusContrat() {
		logger.info("Starting contract status update process");
		List<Contrat> contrats = contratRepository.findAll();
		List<Contrat> contrats15j = new ArrayList<>(); // Initialized to avoid NullPointerException
		List<Contrat> contratsAarchiver = new ArrayList<>(); // Initialized to avoid NullPointerException

		for (Contrat contrat : contrats) {
			Date dateSysteme = new Date();
			if (Boolean.FALSE.equals(contrat.getArchive())) {
				long difference_In_Time = dateSysteme.getTime() - contrat.getDateFinContrat().getTime();
				long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

				if (difference_In_Days == 15) {
					contrats15j.add(contrat);
					logger.info("Contract nearing end (15 days): {}", contrat);
				}
				if (difference_In_Days == 0) {
					contratsAarchiver.add(contrat);
					contrat.setArchive(true);
					contratRepository.save(contrat);
					logger.info("Contract archived: {}", contrat);
				}
			}
		}
		logger.debug("Processed {} contracts: {} nearing end, {} archived", contrats.size(), contrats15j.size(), contratsAarchiver.size());
	}

	@Override
	public float getChiffreAffaireEntreDeuxDates(Date startDate, Date endDate) {
		logger.info("Calculating revenue between {} and {}", startDate, endDate);

		float chiffreAffaireEntreDeuxDates = 0;
		List<Contrat> contrats = contratRepository.findAll();

		for (Contrat contrat : contrats) {
			logger.debug("Contract: {}", contrat);

			// Vérification des contrats non archivés
			if (contrat.getArchive() == null || !contrat.getArchive()) {
				logger.debug("Contract is active and not archived");

				// Vérification si la date de fin est dans la période
				if (contrat.getDateFinContrat() != null) {
					boolean isDateInRange =
							(contrat.getDateFinContrat().equals(startDate) || contrat.getDateFinContrat().after(startDate)) &&
									(contrat.getDateFinContrat().equals(endDate) || contrat.getDateFinContrat().before(endDate));

					if (isDateInRange) {
						logger.debug("Contract within range. Adding montant: {}", contrat.getMontantContrat());
						chiffreAffaireEntreDeuxDates += contrat.getMontantContrat();
					} else {
						logger.debug("Contract with end date {} is not within the date range.", contrat.getDateFinContrat());
					}
				} else {
					logger.debug("Contract has no end date.");
				}
			} else {
				logger.debug("Contract is archived.");
			}
		}

		logger.debug("Calculated revenue: {} for period between {} and {}", chiffreAffaireEntreDeuxDates, startDate, endDate);
		return chiffreAffaireEntreDeuxDates;
	}





}