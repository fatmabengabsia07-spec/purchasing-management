package com.purchasing.converter;

import com.purchasing.dto.CommandeAchatDTO;
import com.purchasing.entity.CommandeAchat;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommandeAchatConverter {

    @Autowired
    private ModelMapper modelMapper;

    public CommandeAchatDTO toDto(CommandeAchat commande) {
        CommandeAchatDTO dto = modelMapper.map(commande, CommandeAchatDTO.class);
        dto.setFournisseurId(commande.getFournisseur().getId());
        return dto;
    }

    public CommandeAchat fromDto(CommandeAchatDTO dto) {
        return modelMapper.map(dto, CommandeAchat.class);
    }

    public List<CommandeAchatDTO> toDtoList(List<CommandeAchat> commandes) {
        return commandes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
