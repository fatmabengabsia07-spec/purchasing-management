package com.purchasing.service;

import com.purchasing.converter.FournisseurConverter;
import com.purchasing.dto.EvaluationDTO;
import com.purchasing.dto.FournisseurDTO;
import com.purchasing.entity.Fournisseur;
import com.purchasing.repository.CommandeAchatRepository;
import com.purchasing.repository.FournisseurRepository;
import com.purchasing.repository.HistoriqueAchatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FournisseurService {

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private HistoriqueAchatsRepository historiqueRepository;

    @Autowired
    private CommandeAchatRepository commandeRepository;

    @Autowired
    private FournisseurConverter fournisseurConverter;

    public List<FournisseurDTO> getAll() {
        return fournisseurConverter.toDtoList(fournisseurRepository.findAll());
    }

    public FournisseurDTO getById(Long id) {
        return fournisseurRepository.findById(id)
                .map(fournisseurConverter::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Fournisseur non trouvé avec id: " + id));
    }

    public FournisseurDTO create(FournisseurDTO dto) {
        Fournisseur entity = fournisseurConverter.fromDto(dto);
        return fournisseurConverter.toDto(fournisseurRepository.save(entity));
    }

    public FournisseurDTO update(Long id, FournisseurDTO dto) {
        final Fournisseur[] result = {null};
        fournisseurRepository.findById(id).ifPresentOrElse(
                existing -> {
                    existing.setNom(dto.getNom());
                    existing.setContact(dto.getContact());
                    existing.setQualiteService(dto.getQualiteService());
                    existing.setNote(dto.getNote());
                    result[0] = fournisseurRepository.save(existing);
                },
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Fournisseur non trouvé avec id: " + id); }
        );
        return fournisseurConverter.toDto(result[0]);
    }

    public void delete(Long id) {
        fournisseurRepository.findById(id).ifPresentOrElse(
                f -> fournisseurRepository.deleteById(id),
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Fournisseur non trouvé avec id: " + id); }
        );
    }

    public List<FournisseurDTO> searchByNom(String nom) {
        return fournisseurConverter.toDtoList(
                fournisseurRepository.findByNomContainingIgnoreCase(nom));
    }

    public List<FournisseurDTO> getTopFournisseurs(Double noteMin) {
        return fournisseurConverter.toDtoList(
                fournisseurRepository.findTopFournisseurs(noteMin));
    }

    public EvaluationDTO evaluerFournisseur(Long id) {
        Fournisseur f = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Fournisseur non trouvé avec id: " + id));

        Double avgDelai = historiqueRepository.getAvgDelaiByFournisseur(id);
        Double totalAchats = commandeRepository.getTotalMontantByFournisseur(id);
        Long nbCommandes = (long) commandeRepository.findByFournisseurId(id).size();

        double noteCalc = calculerNote(f.getQualiteService(), f.getNote(), avgDelai);

        return EvaluationDTO.builder()
                .fournisseurId(id)
                .nomFournisseur(f.getNom())
                .noteGlobale(noteCalc)
                .avgDelaiLivraison(avgDelai != null ? avgDelai : 0.0)
                .nombreCommandes(nbCommandes)
                .totalAchats(totalAchats != null ? totalAchats : 0.0)
                .appreciation(getAppreciation(noteCalc))
                .build();
    }

    public List<EvaluationDTO> comparerFournisseurs() {
        return fournisseurRepository.findAllOrderByNoteDesc().stream()
                .map(f -> evaluerFournisseur(f.getId()))
                .collect(Collectors.toList());
    }

    private double calculerNote(Integer qualite, Double note, Double avgDelai) {
        double score = 0.0;
        if (qualite != null) score += (qualite / 5.0) * 40;
        if (note != null)    score += (note / 10.0) * 40;
        if (avgDelai != null && avgDelai > 0)
            score += Math.max(0, (1 - avgDelai / 30.0)) * 20;
        return Math.round(score * 100.0) / 100.0;
    }

    private String getAppreciation(double note) {
        if (note >= 75) return "Excellent";
        if (note >= 50) return "Bon";
        if (note >= 25) return "Moyen";
        return "Insuffisant";
    }
}
