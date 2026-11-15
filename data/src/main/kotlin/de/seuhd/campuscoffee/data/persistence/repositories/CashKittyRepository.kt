package de.seuhd.campuscoffee.data.persistence.repositories

import de.seuhd.campuscoffee.data.persistence.entities.CashKittyEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

/**
 * Spring Data JPA repository for [CashKittyEntity], providing pessimistic locking for atomic balance updates.
 */
interface CashKittyRepository : JpaRepository<CashKittyEntity, UUID> {
    /**
     * Loads the cash kitty entity by [id] with a pessimistic write lock (`SELECT ... FOR UPDATE`).
     *
     * @param id the singleton cash kitty id
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CashKittyEntity c WHERE c.id = :id")
    fun findByIdForUpdate(
        @Param("id") id: UUID
    ): CashKittyEntity?
}
