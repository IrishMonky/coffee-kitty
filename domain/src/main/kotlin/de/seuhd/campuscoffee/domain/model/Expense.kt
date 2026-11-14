package de.seuhd.campuscoffee.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model of a coffee bean expense recorded by a user. Instances are immutable.
 *
 * The data module assigns [id], [createdAt], and [updatedAt]. They are null on an expense that is not stored yet.
 */
data class Expense(
    override val id: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val user: User,
    val beanName: String,
    val weightGrams: Int,
    val amount: BigDecimal,
    val note: String? = null
) : DomainModel<UUID> {
    /** The validation limits that every new coffee bean expense must meet. */
    companion object {
        /** The maximum length of a bean name. */
        const val MAX_BEAN_NAME_LENGTH = 255

        /** The minimum weight of coffee beans in grams per entry. */
        const val MIN_WEIGHT_GRAMS = 100

        /** The maximum weight of coffee beans in grams per entry. */
        const val MAX_WEIGHT_GRAMS = 1000

        /** The minimum expense amount in euros. */
        val MIN_AMOUNT: BigDecimal = BigDecimal("0.01")

        /** The maximum expense amount in euros. */
        val MAX_AMOUNT: BigDecimal = BigDecimal("1000.00")

        /** The maximum length of an optional note. */
        const val MAX_NOTE_LENGTH = 500
    }
}
