package org.example.zairo.transaction.infrastrucutre.mapper;

import org.example.zairo.transaction.application.dto.*;
import org.mapstruct.*;
import org.example.zairo.transaction.domain.model.Transaction;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // 🔹 Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    Transaction toEntity(TransactionRequest request, UUID userId);

    // 🔹 Entity -> Response
    TransactionResponse toResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateEntityFromDto(TransactionRequest request, @MappingTarget Transaction entity);
}