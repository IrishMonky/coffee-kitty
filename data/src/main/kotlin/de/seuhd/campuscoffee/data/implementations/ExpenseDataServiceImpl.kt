package de.seuhd.campuscoffee.data.implementations

import de.seuhd.campuscoffee.data.mapper.ExpenseEntityMapper
import de.seuhd.campuscoffee.data.persistence.entities.ExpenseEntity
import de.seuhd.campuscoffee.data.persistence.repositories.ExpenseRepository
import de.seuhd.campuscoffee.domain.model.Expense
import de.seuhd.campuscoffee.domain.ports.data.ExpenseDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Data adapter that implements the [ExpenseDataService] port on the `expenses` table.
 */
@Service
class ExpenseDataServiceImpl(
    repository: ExpenseRepository,
    entityMapper: ExpenseEntityMapper
) : CrudDataServiceImpl<Expense, ExpenseEntity, ExpenseRepository, UUID>(
        repository,
        entityMapper,
        Expense::class.java
    ),
    ExpenseDataService {
    @Transactional(readOnly = true)
    override fun getAll(): List<Expense> = super.getAll()

    @Transactional(readOnly = true)
    override fun getById(id: UUID): Expense = super.getById(id)
}
