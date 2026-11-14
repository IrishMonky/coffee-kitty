package de.seuhd.campuscoffee.data.persistence.repositories

import de.seuhd.campuscoffee.data.persistence.entities.ExpenseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

/**
 * Spring Data JPA repository for [ExpenseEntity].
 */
interface ExpenseRepository : JpaRepository<ExpenseEntity, UUID>
