package de.seuhd.campuscoffee.api.controller

import de.seuhd.campuscoffee.api.dtos.CashKittyBalanceDto
import de.seuhd.campuscoffee.api.dtos.PaymentDto
import de.seuhd.campuscoffee.api.dtos.PaymentRequestDto
import de.seuhd.campuscoffee.api.mapper.PaymentDtoMapper
import de.seuhd.campuscoffee.domain.model.persistedId
import de.seuhd.campuscoffee.domain.ports.api.PaymentService
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
 * Controller under `/api/payments` for listing recorded cash deposits, querying the total cash kitty balance, and
 * recording new cash deposits by admin users.
 */
@Tag(name = "Payments", description = "Recording and listing cash deposits and reading the cash kitty balance.")
@RestController
@RequestMapping("/payments")
class PaymentController(
    private val paymentService: PaymentService,
    private val paymentDtoMapper: PaymentDtoMapper
) {
    /**
     * Returns all recorded cash deposits.
     */
    @Operation(summary = "Get all recorded cash deposits.")
    @GetMapping("")
    fun getAll(): ResponseEntity<List<PaymentDto>> =
        ResponseEntity.ok(paymentService.getAll().map { paymentDtoMapper.fromDomain(it) })

    /**
     * Returns the current total balance of the cash kitty.
     */
    @Operation(summary = "Get the total cash kitty balance.")
    @GetMapping("/kitty-balance")
    fun getKittyBalance(): ResponseEntity<CashKittyBalanceDto> =
        ResponseEntity.ok(CashKittyBalanceDto(totalBalance = paymentService.getTotalKittyBalance()))

    /**
     * Records a new cash deposit entered by an admin user and returns 201 Created with the URL of the new payment in
     * the `Location` header.
     *
     * @param dto the cash deposit details
     */
    @Operation(summary = "Record a new cash deposit (admin only).")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    fun create(
        @ApiRequestBody(description = "The cash deposit to record.", required = true)
        @RequestBody
        @Valid dto: PaymentRequestDto
    ): ResponseEntity<PaymentDto> {
        val created =
            paymentService.create(
                recordedByUserId = requireNotNull(dto.recordedByUserId),
                userId = requireNotNull(dto.userId),
                amount = requireNotNull(dto.amount)
            )
        return ResponseEntity.created(locationOf(created.persistedId)).body(paymentDtoMapper.fromDomain(created))
    }

    /**
     * Returns the URL for the `Location` header of a new payment.
     *
     * @param id the id of the new payment
     */
    private fun locationOf(id: UUID): URI =
        ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri()
}
