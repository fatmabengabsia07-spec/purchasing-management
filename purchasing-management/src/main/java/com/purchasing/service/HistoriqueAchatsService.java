package com.purchasing.service;

import com.purchasing.entity.Fournisseur;
import com.purchasing.entity.HistoriqueAchats;
import com.purchasing.repository.FournisseurRepository;
import com.purchasing.repository.HistoriqueAchatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HistoriqueAchatsService {

    @Autowired
    private HistoriqueAchatsRepository historiqueRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    public List<HistoriqueAchats> getAll() {
        return historiqueRepository.findAll();
    }

    public HistoriqueAchats getById(Long id) {
        return historiqueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Historique non trouvé avec id: " + id));
    }

    public List<HistoriqueAchats> getByFournisseur(Long fournisseurId) {
        return historiqueRepository.findByFournisseurId(fournisseurId);
    }

    public HistoriqueAchats create(Long fournisseurId, HistoriqueAchats historique) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Fournisseur non trouvé avec id: " + fournisseurId));
        historique.setFournisseur(fournisseur);
        return historiqueRepository.save(historique);
    }

    public HistoriqueAchats update(Long id, HistoriqueAchats updated) {
        final HistoriqueAchats[] result = {null};
        historiqueRepository.findById(id).ifPresentOrElse(
                existing -> {
                    existing.setProduit(updated.getProduit());
                    existing.setQuantite(updated.getQuantite());
                    existing.setDelaiLivraison(updated.getDelaiLivraison());
                    existing.setDateAchat(updated.getDateAchat());
                    result[0] = historiqueRepository.save(existing);
                },
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Historique non trouvé avec id: " + id); }
        );
        return result[0];
    }

    public void delete(Long id) {
        historiqueRepository.findById(id).ifPresentOrElse(
                h -> historiqueRepository.deleteById(id),
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Historique non trouvé avec id: " + id); }
        );
    }

    public Double getAvgDelai(Long fournisseurId) {
        return historiqueRepository.getAvgDelaiByFournisseur(fournisseurId);
    }
}
