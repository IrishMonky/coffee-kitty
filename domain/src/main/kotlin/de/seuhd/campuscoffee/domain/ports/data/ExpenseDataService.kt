package de.seuhd.campuscoffee.domain.ports.data

import de.seuhd.campuscoffee.domain.model.Expense
import java.util.UUID

/**
 * Data port for persisting [Expense] domain objects.
 */
interface ExpenseDataService : CrudDataService<Expense, UUID>
