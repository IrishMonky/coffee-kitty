package de.seuhd.campuscoffee.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

/**
 * Response DTO representing the current total balance of the cash kitty.
 */
@Schema(description = "Current total balance of the cash kitty in euros.")
data class CashKittyBalanceDto(
    @field:Schema(example = "25.00")
    val totalBalance: BigDecimal = BigDecimal("0.00")
)
