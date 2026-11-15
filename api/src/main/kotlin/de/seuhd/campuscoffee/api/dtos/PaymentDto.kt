package de.seuhd.campuscoffee.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Response DTO representing a recorded cash deposit.
 */
@Schema(description = "A recorded cash deposit.")
data class PaymentDto(
    @field:Schema(accessMode = Schema.AccessMode.READ_ONLY)
    override val id: UUID? = null,
    @field:Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val createdAt: LocalDateTime? = null,
    @field:Schema(accessMode = Schema.AccessMode.READ_ONLY)
    val updatedAt: LocalDateTime? = null,
    val userId: UUID,
    val recordedByUserId: UUID,
    val amount: BigDecimal
) : Dto<UUID>
