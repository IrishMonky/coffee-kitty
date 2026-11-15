package de.seuhd.campuscoffee.domain.ports.api

import de.seuhd.campuscoffee.domain.model.Payment
import java.math.BigDecimal
import java.util.UUID

/**
 * API port for recording cash deposits and querying recorded payments and the total cash kitty balance.
 */
interface PaymentService {
    /**
     * Records a cash deposit entered by an admin user for a target user, increasing both the target user's balance
     * and the total cash kitty balance by [amount].
     *
     * @param recordedByUserId the id of the admin user recording the deposit
     * @param userId the id of the user whose balance is credited
     * @param amount the deposit amount in euros
     */
    fun create(
        recordedByUserId: UUID,
        userId: UUID,
        amount: BigDecimal
    ): Payment

    /**
     * Returns all recorded cash deposits.
     */
    fun getAll(): List<Payment>

    /**
     * Returns the current total balance of the cash kitty in euros.
     */
    fun getTotalKittyBalance(): BigDecimal
}
