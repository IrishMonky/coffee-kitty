package de.seuhd.campuscoffee.data.mapper

import de.seuhd.campuscoffee.data.persistence.entities.CashKittyEntity
import de.seuhd.campuscoffee.domain.model.CashKitty
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

/**
 * MapStruct mapper between [CashKitty] and [CashKittyEntity].
 */
@Mapper(componentModel = "spring")
interface CashKittyEntityMapper : EntityMapper<CashKitty, CashKittyEntity> {
    @Mapping(target = "totalBalance", source = "totalBalance")
    override fun fromEntity(source: CashKittyEntity): CashKitty

    @Mapping(target = "totalBalance", source = "totalBalance")
    override fun toEntity(source: CashKitty): CashKittyEntity

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "totalBalance", source = "totalBalance")
    override fun updateEntity(
        source: CashKitty,
        @MappingTarget target: CashKittyEntity
    )
}
