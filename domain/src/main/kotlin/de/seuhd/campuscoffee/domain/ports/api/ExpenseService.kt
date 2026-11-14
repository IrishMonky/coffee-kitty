package de.seuhd.campuscoffee.domain.ports.api

import de.seuhd.campuscoffee.domain.model.Expense
import java.math.BigDecimal
import java.util.UUID

/**
 * API port for managing coffee bean expenses.
 */
interface ExpenseService {
    /**
     * Records a new coffee bean expense for a user and credits the amount to the user's balance.
     *
     * @param userId the id of the user who paid for the expense
     * @param beanName the name of the purchased coffee beans
     * @param weightGrams the weight of the coffee beans in grams
     * @param amount the expense amount in euros
     * @param note an optional note describing the expense
     * @return the created expense
     */
    fun create(
        userId: UUID,
        beanName: String,
        weightGrams: Int,
        amount: BigDecimal,
        note: String? = null
    ): Expense

    /**
     * Returns all recorded expenses.
     *
     * @return a list of all expenses
     */
    fun getAll(): List<Expense>
}
