package de.seuhd.campuscoffee.domain.ports.data

import de.seuhd.campuscoffee.domain.model.CashKitty

/**
 * Data port for reading and updating the singleton [CashKitty] balance.
 */
interface CashKittyDataService {
    /**
     * Returns the current [CashKitty] state.
     */
    fun get(): CashKitty

    /**
     * Returns the current [CashKitty] state with a pessimistic write lock for atomic updates.
     */
    fun getForUpdate(): CashKitty

    /**
     * Persists the updated [cashKitty] state and returns the saved domain object.
     *
     * @param cashKitty the updated cash kitty state
     */
    fun upsert(cashKitty: CashKitty): CashKitty

    /**
     * Resets the total cash kitty balance back to zero.
     */
    fun resetBalance()
}
