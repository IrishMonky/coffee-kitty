package de.seuhd.campuscoffee.api.mapper

import de.seuhd.campuscoffee.api.dtos.PaymentDto
import de.seuhd.campuscoffee.domain.model.Payment
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * MapStruct mapper converting [Payment] domain objects to [PaymentDto] responses.
 */
@Mapper(componentModel = "spring")
interface PaymentDtoMapper {
    /**
     * Maps a [Payment] domain object to a [PaymentDto].
     *
     * @param source the domain payment
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "recordedByUserId", source = "recordedBy.id")
    fun fromDomain(source: Payment): PaymentDto
}
