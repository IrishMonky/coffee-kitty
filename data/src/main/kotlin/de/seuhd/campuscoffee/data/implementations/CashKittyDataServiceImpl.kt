package de.seuhd.campuscoffee.data.implementations

import de.seuhd.campuscoffee.data.mapper.CashKittyEntityMapper
import de.seuhd.campuscoffee.data.persistence.entities.CashKittyEntity
import de.seuhd.campuscoffee.data.persistence.repositories.CashKittyRepository
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException
import de.seuhd.campuscoffee.domain.model.CashKitty
import de.seuhd.campuscoffee.domain.ports.data.CashKittyDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/**
 * JPA data adapter implementing [CashKittyDataService] for the singleton cash kitty record.
 */
@Service
class CashKittyDataServiceImpl(
    private val repository: CashKittyRepository,
    private val mapper: CashKittyEntityMapper
) : CashKittyDataService {
    @Transactional(readOnly = true)
    override fun get(): CashKitty {
        val entity =
            repository.findById(CashKitty.DEFAULT_ID).orElseThrow {
                NotFoundException(CashKitty::class.java, CashKitty.DEFAULT_ID)
            }
        return mapper.fromEntity(entity)
    }

    @Transactional
    override fun getForUpdate(): CashKitty {
        val entity =
            repository.findByIdForUpdate(CashKitty.DEFAULT_ID)
                ?: throw NotFoundException(CashKitty::class.java, CashKitty.DEFAULT_ID)
        return mapper.fromEntity(entity)
    }

    @Transactional
    override fun upsert(cashKitty: CashKitty): CashKitty {
        val entity =
            repository.findById(CashKitty.DEFAULT_ID).orElseGet {
                CashKittyEntity().apply { id = CashKitty.DEFAULT_ID }
            }
        entity.totalBalance = cashKitty.totalBalance
        return mapper.fromEntity(repository.saveAndFlush(entity))
    }

    @Transactional
    override fun resetBalance() {
        val entity =
            repository.findById(CashKitty.DEFAULT_ID).orElseGet {
                CashKittyEntity().apply { id = CashKitty.DEFAULT_ID }
            }
        entity.totalBalance = BigDecimal("0.00")
        repository.saveAndFlush(entity)
    }
}
