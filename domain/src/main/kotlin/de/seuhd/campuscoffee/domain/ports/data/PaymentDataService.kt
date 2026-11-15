package de.seuhd.campuscoffee.domain.ports.data

import de.seuhd.campuscoffee.domain.model.Payment
import java.util.UUID

/**
 * Data port for persisting and retrieving [Payment] domain objects.
 */
interface PaymentDataService : CrudDataService<Payment, UUID>
