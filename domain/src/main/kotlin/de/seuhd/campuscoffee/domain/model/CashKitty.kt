package de.seuhd.campuscoffee.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing the singleton cash kitty holding the total physical cash balance.
 */
data class CashKitty(
    override val id: UUID? = DEFAULT_ID,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val totalBalance: BigDecimal
) : DomainModel<UUID> {
    companion object {
        /** The fixed singleton identifier of the cash kitty record. */
        val DEFAULT_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }
}
