package de.seuhd.campuscoffee.data.mapper

import de.seuhd.campuscoffee.data.persistence.entities.ExpenseEntity
import de.seuhd.campuscoffee.domain.model.Expense
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget

/**
 * MapStruct mapper between [Expense] domain objects and [ExpenseEntity] JPA entities. [toEntity] and [updateEntity]
 * set [ExpenseEntity.user] to the stored user, which [EntityReferenceResolver] loads by the id of [Expense.user].
 * [fromEntity] maps the user with all fields through [UserEntityMapper]. [updateEntity] does not copy the id or the
 * timestamps (see [EntityMapper.updateEntity]).
 */
@Mapper(componentModel = "spring", uses = [UserEntityMapper::class, EntityReferenceResolver::class])
interface ExpenseEntityMapper : EntityMapper<Expense, ExpenseEntity> {
    @Mapping(target = "user", source = "user.id")
    override fun toEntity(source: Expense): ExpenseEntity

    @Mapping(target = "user", source = "user.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    override fun updateEntity(
        source: Expense,
        @MappingTarget target: ExpenseEntity
    )
}
