package de.seuhd.campuscoffee.api.controller

import de.seuhd.campuscoffee.api.dtos.ExpenseDto
import de.seuhd.campuscoffee.api.dtos.ExpenseRequestDto
import de.seuhd.campuscoffee.api.mapper.ExpenseDtoMapper
import de.seuhd.campuscoffee.domain.model.persistedId
import de.seuhd.campuscoffee.domain.ports.api.ExpenseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.UUID
import io.swagger.v3.oas.annotations.parameters.RequestBody as ApiRequestBody

/**
 * Controller under `/api/expenses` that lists all recorded coffee bean expenses and creates new expenses.
 */
@Tag(name = "Expenses", description = "Listing and recording coffee bean expenses.")
@RestController
@RequestMapping("/expenses")
class ExpenseController(
    private val expenseService: ExpenseService,
    private val expenseDtoMapper: ExpenseDtoMapper
) {
    /**
     * Returns all recorded coffee bean expenses.
     */
    @Operation(summary = "Get all expenses.")
    @GetMapping("")
    fun getAll(): ResponseEntity<List<ExpenseDto>> =
        ResponseEntity.ok(expenseService.getAll().map { expenseDtoMapper.fromDomain(it) })

    /**
     * Records a new coffee bean expense and returns 201 Created with the URL of the new expense in the
     * `Location` header.
     *
     * @param dto the expense details
     */
    @Operation(summary = "Record a new coffee bean expense.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    fun create(
        @ApiRequestBody(description = "The expense to record.", required = true)
        @RequestBody
        @Valid dto: ExpenseRequestDto
    ): ResponseEntity<ExpenseDto> {
        val created =
            expenseService.create(
                userId = requireNotNull(dto.userId),
                beanName = requireNotNull(dto.beanName),
                weightGrams = requireNotNull(dto.weightGrams),
                amount = requireNotNull(dto.amount),
                note = dto.note
            )
        return ResponseEntity.created(locationOf(created.persistedId)).body(expenseDtoMapper.fromDomain(created))
    }

    /**
     * Returns the URL for the `Location` header of a new expense.
     *
     * @param id the id of the new expense
     */
    private fun locationOf(id: UUID): URI =
        ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri()
}
