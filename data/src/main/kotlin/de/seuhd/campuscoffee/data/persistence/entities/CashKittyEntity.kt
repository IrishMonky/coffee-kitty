package de.seuhd.campuscoffee.data.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.Table
import java.math.BigDecimal

/**
 * JPA entity representing the singleton cash kitty table `cash_kitty` that holds the total physical cash balance.
 */
@jakarta.persistence.Entity
@Table(name = "cash_kitty")
class CashKittyEntity : Entity() {
    @Column(name = "total_balance", nullable = false, precision = 10, scale = 2)
    var totalBalance: BigDecimal = BigDecimal("0.00")
}
