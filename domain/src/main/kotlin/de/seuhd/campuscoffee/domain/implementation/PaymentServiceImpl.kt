package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.exceptions.ForbiddenException
import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.Payment
import de.seuhd.campuscoffee.domain.ports.api.PaymentService
import de.seuhd.campuscoffee.domain.ports.data.CashKittyDataService
import de.seuhd.campuscoffee.domain.ports.data.PaymentDataService
import de.seuhd.campuscoffee.domain.ports.data.UserDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

/**
 * Domain implementation of [PaymentService]. Validates admin permissions and deposit limits, and atomically
 * increases both the recipient user's balance and the total cash kitty balance.
 */
@Service
class PaymentServiceImpl(
    private val paymentDataService: PaymentDataService,
    private val cashKittyDataService: CashKittyDataService,
    private val userDataService: UserDataService
) : PaymentService {
    @Transactional
    override fun create(
        recordedByUserId: UUID,
        userId: UUID,
        amount: BigDecimal
    ): Payment {
        validateAmount(amount)

        val recordedBy = userDataService.getById(recordedByUserId)
        if (!recordedBy.admin) {
            throw ForbiddenException("Only admin users may record cash deposits.")
        }

        val recipient = userDataService.getByIdForUpdate(userId)
        val updatedRecipient = userDataService.upsert(recipient.copy(balance = recipient.balance.add(amount)))

        val kitty = cashKittyDataService.getForUpdate()
        cashKittyDataService.upsert(kitty.copy(totalBalance = kitty.totalBalance.add(amount)))

        val payment =
            Payment(
                user = updatedRecipient,
                recordedBy = recordedBy,
                amount = amount
            )
        return paymentDataService.upsert(payment)
    }

    override fun getAll(): List<Payment> = paymentDataService.getAll()

    override fun getTotalKittyBalance(): BigDecimal = cashKittyDataService.get().totalBalance

    /**
     * Validates that [amount] lies within [Payment.MIN_AMOUNT] and [Payment.MAX_AMOUNT].
     *
     * @param amount the deposit amount to validate
     * @throws ValidationException if [amount] is outside the permitted range
     */
    private fun validateAmount(amount: BigDecimal) {
        if (amount < Payment.MIN_AMOUNT || amount > Payment.MAX_AMOUNT) {
            throw ValidationException(
                "The amount must be between ${Payment.MIN_AMOUNT} and ${Payment.MAX_AMOUNT}."
            )
        }
    }
}
