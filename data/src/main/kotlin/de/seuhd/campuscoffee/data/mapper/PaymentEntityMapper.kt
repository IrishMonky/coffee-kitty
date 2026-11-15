package de.seuhd.campuscoffee.data.mapper

import de.seuhd.campuscoffee.data.persistence.entities.PaymentEntity
import de.seuhd.campuscoffee.domain.model.Payment
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

/**
 * MapStruct mapper between [Payment] and [PaymentEntity]. Uses [EntityReferenceResolver] to resolve both `user` and
 * `recordedBy` by their ids without overwriting existing user rows.
 */
@Mapper(componentModel = "spring", uses = [UserEntityMapper::class, EntityReferenceResolver::class])
interface PaymentEntityMapper : EntityMapper<Payment, PaymentEntity> {
    @Mapping(target = "user", source = "user.id")
    @Mapping(target = "recordedBy", source = "recordedBy.id")
    override fun toEntity(domain: Payment): PaymentEntity

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user.id")
    @Mapping(target = "recordedBy", source = "recordedBy.id")
    override fun updateEntity(
        domain: Payment,
        @MappingTarget entity: PaymentEntity
    )
}
