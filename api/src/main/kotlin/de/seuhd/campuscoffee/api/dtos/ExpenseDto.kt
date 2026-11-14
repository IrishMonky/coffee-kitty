package de.seuhd.campuscoffee.api.dtos

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

/**
 * Response body representing a recorded coffee bean expense.
 */
data class ExpenseDto(
    @field:Schema(accessMode = Schema.AccessMode.READ_ONLY)
    override val id: UUID? = null,
    @field:Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "When the expense was recorded, in UTC.")
    val createdAt: LocalDateTime? = null,
    @field:Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "When the expense was last changed, in UTC.")
    val updatedAt: LocalDateTime? = null,
    val userId: UUID,
    val loginName: String,
    val beanName: String,
    val weightGrams: Int,
    val amount: BigDecimal,
    val note: String? = null
) : Dto<UUID>
