package com.purchasing.converter;

import com.purchasing.dto.FournisseurDTO;
import com.purchasing.entity.Fournisseur;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FournisseurConverter {

    @Autowired
    private ModelMapper modelMapper;

    public FournisseurDTO toDto(Fournisseur fournisseur) {
        return modelMapper.map(fournisseur, FournisseurDTO.class);
    }

    public Fournisseur fromDto(FournisseurDTO dto) {
        return modelMapper.map(dto, Fournisseur.class);
    }

    public List<FournisseurDTO> toDtoList(List<Fournisseur> fournisseurs) {
        return fournisseurs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
