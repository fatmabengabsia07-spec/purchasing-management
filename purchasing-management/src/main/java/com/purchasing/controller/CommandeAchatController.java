package com.purchasing.controller;

import com.purchasing.dto.CommandeAchatDTO;
import com.purchasing.service.CommandeAchatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@Tag(name = "Commandes d'achat", description = "Gestion des commandes fournisseurs")
public class CommandeAchatController {

    @Autowired
    private CommandeAchatService commandeService;

    @GetMapping
    @Operation(summary = "Lister toutes les commandes")
    public ResponseEntity<List<CommandeAchatDTO>> getAll() {
        return ResponseEntity.ok(commandeService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une commande par ID")
    public ResponseEntity<CommandeAchatDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.getById(id));
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    @Operation(summary = "Commandes par fournisseur")
    public ResponseEntity<List<CommandeAchatDTO>> getByFournisseur(@PathVariable Long fournisseurId) {
        return ResponseEntity.ok(commandeService.getByFournisseur(fournisseurId));
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Commandes par statut")
    public ResponseEntity<List<CommandeAchatDTO>> getByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(commandeService.getByStatut(statut));
    }

    @PostMapping
    @Operation(summary = "Créer une commande")
    public ResponseEntity<CommandeAchatDTO> create(@Valid @RequestBody CommandeAchatDTO dto) {
        return new ResponseEntity<>(commandeService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une commande")
    public ResponseEntity<CommandeAchatDTO> update(
            @PathVariable Long id, @Valid @RequestBody CommandeAchatDTO dto) {
        return ResponseEntity.ok(commandeService.update(id, dto));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut")
    public ResponseEntity<CommandeAchatDTO> updateStatut(
            @PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(commandeService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
