package de.seuhd.campuscoffee.domain.implementation

import de.seuhd.campuscoffee.domain.exceptions.NotFoundException
import de.seuhd.campuscoffee.domain.exceptions.ValidationException
import de.seuhd.campuscoffee.domain.model.Expense
import de.seuhd.campuscoffee.domain.model.User
import de.seuhd.campuscoffee.domain.ports.data.ExpenseDataService
import de.seuhd.campuscoffee.domain.ports.data.UserDataService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID
import kotlin.reflect.jvm.javaMethod

/**
 * Unit tests for [ExpenseServiceImpl] with Mockito mocks of [ExpenseDataService] and [UserDataService].
 */
class ExpenseServiceTest {
    private val expenseDataService: ExpenseDataService = mock()
    private val userDataService: UserDataService = mock()
    private val service = ExpenseServiceImpl(expenseDataService, userDataService)

    private val userId: UUID = UUID(0L, 1L)
    private val expenseId: UUID = UUID(0L, 100L)

    private fun userWithBalance(balance: BigDecimal = BigDecimal("10.00")) =
        User(
            id = userId,
            loginName = "max",
            emailAddress = "max@se.de",
            firstName = "Max",
            lastName = "M",
            balance = balance
        )

    private fun verifyNothingStored() {
        verify(userDataService, never()).upsert(any())
        verify(expenseDataService, never()).upsert(any())
    }

    @Test
    fun `getAll returns every expense from the data port`() {
        val expense =
            Expense(
                id = expenseId,
                user = userWithBalance(),
                beanName = "Espresso Roast",
                weightGrams = 500,
                amount = BigDecimal("14.50")
            )
        whenever(expenseDataService.getAll()).thenReturn(listOf(expense))

        assertThat(service.getAll()).containsExactly(expense)
    }

    @Test
    fun `create credits the amount to the locked user balance and stores the trimmed expense`() {
        val lockedUser = userWithBalance(BigDecimal("10.00"))
        val updatedUser = userWithBalance(BigDecimal("24.50"))
        whenever(userDataService.getByIdForUpdate(userId)).thenReturn(lockedUser)
        whenever(userDataService.upsert(any())).thenAnswer { it.arguments[0] as User }
        whenever(expenseDataService.upsert(any())).thenAnswer { (it.arguments[0] as Expense).copy(id = expenseId) }

        val created =
            service.create(
                userId = userId,
                beanName = "  Espresso Roast  ",
                weightGrams = 500,
                amount = BigDecimal("14.50"),
                note = "Für das Institut"
            )

        assertThat(created.id).isEqualTo(expenseId)
        assertThat(created.beanName).isEqualTo("Espresso Roast")
        assertThat(created.user.balance).isEqualByComparingTo("24.50")
        inOrder(userDataService, expenseDataService) {
            verify(userDataService).getByIdForUpdate(userId)
            verify(userDataService).upsert(updatedUser)
            verify(expenseDataService).upsert(
                Expense(
                    user = updatedUser,
                    beanName = "Espresso Roast",
                    weightGrams = 500,
                    amount = BigDecimal("14.50"),
                    note = "Für das Institut"
                )
            )
        }
    }

    @Test
    fun `create is transactional with REQUIRED propagation`() {
        val create = requireNotNull(ExpenseServiceImpl::create.javaMethod)

        assertThat(create.getAnnotation(Transactional::class.java)?.propagation).isEqualTo(Propagation.REQUIRED)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   "])
    fun `create with a blank bean name throws ValidationException and stores nothing`(beanName: String) {
        assertThrows<ValidationException> {
            service.create(userId, beanName, 500, BigDecimal("14.50"))
        }
        verifyNothingStored()
    }

    @Test
    fun `create with a bean name of 256 characters throws ValidationException and stores nothing`() {
        assertThrows<ValidationException> {
            service.create(userId, "a".repeat(256), 500, BigDecimal("14.50"))
        }
        verifyNothingStored()
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 99, 1001])
    fun `create with a weight outside 100 to 1000 grams throws ValidationException and stores nothing`(weight: Int) {
        assertThrows<ValidationException> {
            service.create(userId, "Espresso", weight, BigDecimal("14.50"))
        }
        verifyNothingStored()
    }

    @ParameterizedTest
    @ValueSource(strings = ["-1.00", "0.00", "1000.01"])
    fun `create with an amount outside 0_01 to 1000_00 throws ValidationException and stores nothing`(amount: String) {
        assertThrows<ValidationException> {
            service.create(userId, "Espresso", 500, BigDecimal(amount))
        }
        verifyNothingStored()
    }

    @Test
    fun `create with a note of 501 characters throws ValidationException and stores nothing`() {
        assertThrows<ValidationException> {
            service.create(userId, "Espresso", 500, BigDecimal("14.50"), "a".repeat(501))
        }
        verifyNothingStored()
    }

    @Test
    fun `create for an unknown user throws NotFoundException and stores nothing`() {
        whenever(userDataService.getByIdForUpdate(userId))
            .thenThrow(NotFoundException(User::class.java, userId))

        assertThrows<NotFoundException> {
            service.create(userId, "Espresso", 500, BigDecimal("14.50"))
        }
        verifyNothingStored()
    }
}
