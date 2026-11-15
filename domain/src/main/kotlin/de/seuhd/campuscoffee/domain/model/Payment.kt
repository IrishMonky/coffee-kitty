package de.seuhd.campuscoffee.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model of a cash deposit recorded by an admin user for a group member. Instances are immutable.
 */
data class Payment(
    override val id: UUID? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val user: User,
    val recordedBy: User,
    val amount: BigDecimal
) : DomainModel<UUID> {
    /** The limits that every cash deposit must meet. */
    companion object {
        /** The minimum valid deposit amount in euros. */
        val MIN_AMOUNT: BigDecimal = BigDecimal("0.01")

        /** The maximum valid deposit amount in euros. */
        val MAX_AMOUNT: BigDecimal = BigDecimal("1000.00")
    }
}
