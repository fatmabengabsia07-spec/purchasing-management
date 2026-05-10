package com.purchasing.service;

import com.purchasing.entity.CommandeAchat;
import com.purchasing.entity.LigneCommandeAchat;
import com.purchasing.repository.CommandeAchatRepository;
import com.purchasing.repository.LigneCommandeAchatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LigneCommandeAchatService {

    @Autowired
    private LigneCommandeAchatRepository ligneRepository;

    @Autowired
    private CommandeAchatRepository commandeRepository;

    public List<LigneCommandeAchat> getAll() {
        return ligneRepository.findAll();
    }

    public LigneCommandeAchat getById(Long id) {
        return ligneRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ligne non trouvée avec id: " + id));
    }

    public List<LigneCommandeAchat> getByCommande(Long commandeId) {
        return ligneRepository.findByCommandeId(commandeId);
    }

    public LigneCommandeAchat create(Long commandeId, LigneCommandeAchat ligne) {
        CommandeAchat commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Commande non trouvée avec id: " + commandeId));
        ligne.setCommande(commande);
        return ligneRepository.save(ligne);
    }

    public LigneCommandeAchat update(Long id, LigneCommandeAchat updated) {
        final LigneCommandeAchat[] result = {null};
        ligneRepository.findById(id).ifPresentOrElse(
                existing -> {
                    existing.setProduit(updated.getProduit());
                    existing.setQuantite(updated.getQuantite());
                    existing.setPrixUnitaire(updated.getPrixUnitaire());
                    result[0] = ligneRepository.save(existing);
                },
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ligne non trouvée avec id: " + id); }
        );
        return result[0];
    }

    public void delete(Long id) {
        ligneRepository.findById(id).ifPresentOrElse(
                l -> ligneRepository.deleteById(id),
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ligne non trouvée avec id: " + id); }
        );
    }
}
