package de.seuhd.campuscoffee.api.dtos

import de.seuhd.campuscoffee.domain.model.Expense
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

/**
 * Request body for recording a new coffee bean expense.
 */
data class ExpenseRequestDto(
    @field:NotNull
    val userId: UUID?,
    @field:NotBlank
    @field:Size(max = Expense.MAX_BEAN_NAME_LENGTH, message = "must have at most {max} characters")
    val beanName: String?,
    @field:NotNull
    @field:Min(value = Expense.MIN_WEIGHT_GRAMS.toLong(), message = "must be at least {value}")
    @field:Max(value = Expense.MAX_WEIGHT_GRAMS.toLong(), message = "must be at most {value}")
    val weightGrams: Int?,
    @field:NotNull
    @field:DecimalMin(value = "0.01", message = "must be at least {value}")
    @field:DecimalMax(value = "1000.00", message = "must be at most {value}")
    val amount: BigDecimal?,
    @field:Size(max = Expense.MAX_NOTE_LENGTH, message = "must have at most {max} characters")
    val note: String? = null
)
