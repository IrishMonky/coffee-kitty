package de.seuhd.campuscoffee.data.persistence.entities

import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

/**
 * JPA entity of a coffee bean expense recorded by a user.
 */
@jakarta.persistence.Entity
@Table(name = "expenses")
class ExpenseEntity : Entity() {
    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "user_id")
    var user: UserEntity? = null

    @field:Column(name = "bean_name")
    var beanName: String? = null

    @field:Column(name = "weight_grams")
    var weightGrams: Int? = null

    @field:Column(name = "amount", precision = 10, scale = 2)
    var amount: BigDecimal? = null

    @field:Column(name = "note")
    var note: String? = null
}
