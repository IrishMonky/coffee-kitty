package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.Expense
import de.seuhd.campuscoffee.domain.ports.api.ExpenseService
import de.seuhd.campuscoffee.domain.ports.data.ExpenseDataService
import de.seuhd.campuscoffee.domain.ports.data.UserDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

/**
 * Domain implementation of [ExpenseService]. Records coffee bean expenses and credits the amount
 * to the paying user's balance within a single transaction.
 */
@Service
class ExpenseServiceImpl(
    private val expenseDataService: ExpenseDataService,
    private val userDataService: UserDataService
) : ExpenseService {
    @Transactional
    override fun create(
        userId: UUID,
        beanName: String,
        weightGrams: Int,
        amount: BigDecimal,
        note: String?
    ): Expense {
        validate(beanName, weightGrams, amount, note)
        val user = userDataService.getByIdForUpdate(userId)
        val updatedUser = userDataService.upsert(user.copy(balance = user.balance.add(amount)))
        val expense =
            Expense(
                user = updatedUser,
                beanName = beanName.trim(),
                weightGrams = weightGrams,
                amount = amount,
                note = note
            )
        return expenseDataService.upsert(expense)
    }

    override fun getAll(): List<Expense> = expenseDataService.getAll()

    /**
     * Validates all input fields of a new expense against the domain limits.
     *
     * @param beanName the name of the purchased coffee beans
     * @param weightGrams the weight of the coffee beans in grams
     * @param amount the expense amount in euros
     * @param note an optional note describing the expense
     * @throws ValidationException if any field violates the domain constraints
     */
    private fun validate(
        beanName: String,
        weightGrams: Int,
        amount: BigDecimal,
        note: String?
    ) {
        val trimmedName = beanName.trim()
        val errorMessage =
            when {
                trimmedName.isEmpty() || trimmedName.length > Expense.MAX_BEAN_NAME_LENGTH -> {
                    "The bean name must be between 1 and ${Expense.MAX_BEAN_NAME_LENGTH} characters."
                }

                weightGrams !in Expense.MIN_WEIGHT_GRAMS..Expense.MAX_WEIGHT_GRAMS -> {
                    "The weight must be between ${Expense.MIN_WEIGHT_GRAMS} g and ${Expense.MAX_WEIGHT_GRAMS} g."
                }

                amount < Expense.MIN_AMOUNT || amount > Expense.MAX_AMOUNT -> {
                    "The amount must be between ${Expense.MIN_AMOUNT} and ${Expense.MAX_AMOUNT}."
                }

                note != null && note.length > Expense.MAX_NOTE_LENGTH -> {
                    "The note must not exceed ${Expense.MAX_NOTE_LENGTH} characters."
                }

                else -> {
                    null
                }
            }
        if (errorMessage != null) {
            throw ValidationException(errorMessage)
        }
    }
}
