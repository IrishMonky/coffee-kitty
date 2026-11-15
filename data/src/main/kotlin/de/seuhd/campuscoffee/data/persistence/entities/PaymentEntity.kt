package de.seuhd.campuscoffee.data.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

/**
 * JPA entity of a recorded cash deposit. Maps to the `payments` table created by
 * `V6__create_payments_and_cash_kitty.sql`.
 */
@jakarta.persistence.Entity
@Table(name = "payments")
class PaymentEntity : Entity() {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recorded_by_user_id", nullable = false)
    var recordedBy: UserEntity? = null

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal = BigDecimal("0.00")
}
