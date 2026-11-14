package de.seuhd.campuscoffee.api.mapper

import de.seuhd.campuscoffee.api.dtos.ExpenseDto
import de.seuhd.campuscoffee.domain.model.Expense
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * MapStruct mapper from [Expense] domain objects to [ExpenseDto] response objects.
 */
@Mapper(componentModel = "spring")
interface ExpenseDtoMapper {
    /**
     * Maps an [Expense] domain object to an [ExpenseDto].
     *
     * @param source the domain expense
     * @return the mapped response DTO
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "loginName", source = "user.loginName")
    fun fromDomain(source: Expense): ExpenseDto
}
