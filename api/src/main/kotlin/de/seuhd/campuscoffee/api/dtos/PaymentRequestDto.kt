package de.seuhd.campuscoffee.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

/**
 * Request DTO for recording a cash deposit via `POST /api/payments`.
 */
@Schema(description = "Request payload for recording a cash deposit by an admin user.")
data class PaymentRequestDto(
    @field:Schema(
        description = "Id of the admin user recording the deposit.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull
    val recordedByUserId: UUID?,
    @field:Schema(
        description = "Id of the user whose balance is credited.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull
    val userId: UUID?,
    @field:Schema(
        description = "Deposit amount in euros (0.01 to 1000.00).",
        example = "10.00",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @field:NotNull
    @field:DecimalMin("0.01")
    @field:DecimalMax("1000.00")
    val amount: BigDecimal?
)
