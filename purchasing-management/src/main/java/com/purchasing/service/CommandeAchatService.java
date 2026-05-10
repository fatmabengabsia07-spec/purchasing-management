package com.purchasing.service;

import com.purchasing.converter.CommandeAchatConverter;
import com.purchasing.dto.CommandeAchatDTO;
import com.purchasing.entity.CommandeAchat;
import com.purchasing.entity.Fournisseur;
import com.purchasing.repository.CommandeAchatRepository;
import com.purchasing.repository.FournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommandeAchatService {

    @Autowired
    private CommandeAchatRepository commandeRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private CommandeAchatConverter commandeConverter;

    public List<CommandeAchatDTO> getAll() {
        return commandeConverter.toDtoList(commandeRepository.findAll());
    }

    public CommandeAchatDTO getById(Long id) {
        return commandeRepository.findById(id)
                .map(commandeConverter::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Commande non trouvée avec id: " + id));
    }

    public List<CommandeAchatDTO> getByFournisseur(Long fournisseurId) {
        return commandeConverter.toDtoList(commandeRepository.findByFournisseurId(fournisseurId));
    }

    public List<CommandeAchatDTO> getByStatut(String statut) {
        return commandeConverter.toDtoList(commandeRepository.findByStatut(statut));
    }

    public CommandeAchatDTO create(CommandeAchatDTO dto) {
        Fournisseur f = fournisseurRepository.findById(dto.getFournisseurId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Fournisseur non trouvé: " + dto.getFournisseurId()));
        CommandeAchat commande = CommandeAchat.builder()
                .fournisseur(f).date(dto.getDate())
                .statut(dto.getStatut()).montant(dto.getMontant())
                .build();
        return commandeConverter.toDto(commandeRepository.save(commande));
    }

    public CommandeAchatDTO update(Long id, CommandeAchatDTO dto) {
        final CommandeAchat[] result = {null};
        commandeRepository.findById(id).ifPresentOrElse(
                existing -> {
                    Fournisseur f = fournisseurRepository.findById(dto.getFournisseurId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Fournisseur non trouvé: " + dto.getFournisseurId()));
                    existing.setFournisseur(f);
                    existing.setDate(dto.getDate());
                    existing.setStatut(dto.getStatut());
                    existing.setMontant(dto.getMontant());
                    result[0] = commandeRepository.save(existing);
                },
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Commande non trouvée avec id: " + id); }
        );
        return commandeConverter.toDto(result[0]);
    }

    public CommandeAchatDTO updateStatut(Long id, String statut) {
        final CommandeAchat[] result = {null};
        commandeRepository.findById(id).ifPresentOrElse(
                commande -> {
                    commande.setStatut(statut);
                    result[0] = commandeRepository.save(commande);
                },
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Commande non trouvée avec id: " + id); }
        );
        return commandeConverter.toDto(result[0]);
    }

    public void delete(Long id) {
        commandeRepository.findById(id).ifPresentOrElse(
                c -> commandeRepository.deleteById(id),
                () -> { throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Commande non trouvée avec id: " + id); }
        );
    }
}
