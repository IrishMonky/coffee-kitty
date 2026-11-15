package de.seuhd.campuscoffee.tests.system

import de.seuhd.campuscoffee.api.dtos.CashKittyBalanceDto
import de.seuhd.campuscoffee.api.dtos.PaymentDto
import de.seuhd.campuscoffee.api.dtos.UserDto
import de.seuhd.campuscoffee.api.exceptions.ErrorResponse
import de.seuhd.campuscoffee.domain.model.persistedId
import de.seuhd.campuscoffee.tests.SystemTestUtils.client
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.returnResult
import java.math.BigDecimal
import java.util.UUID

/**
 * System tests for `/api/payments` covering `REQ-PAY-01` (Record Cash Deposit).
 */
class PaymentSystemTests : AbstractSystemTest() {
    private fun newPayment(
        recordedByUserId: UUID,
        userId: UUID,
        amount: BigDecimal = BigDecimal("10.00")
    ): Map<String, Any> =
        mapOf(
            "recordedByUserId" to recordedByUserId.toString(),
            "userId" to userId.toString(),
            "amount" to amount
        )

    private fun createPayment(body: Map<String, Any>) =
        client()
            .post()
            .uri("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .exchange()

    @Test
    fun `recording a cash deposit by an admin increases both the user balance and the cash kitty balance`() {
        val admin = fixtureUser("jane_doe")
        val target = fixtureUser("lisa_lee")

        val createResult =
            createPayment(
                newPayment(
                    recordedByUserId = admin.persistedId,
                    userId = target.persistedId,
                    amount = BigDecimal("10.00")
                )
            ).returnResult<PaymentDto>()

        assertThat(createResult.status.value()).isEqualTo(201)
        val created = createResult.responseBody!!
        assertThat(created.id).isNotNull()
        assertThat(created.userId).isEqualTo(target.persistedId)
        assertThat(created.recordedByUserId).isEqualTo(admin.persistedId)
        assertThat(created.amount).isEqualByComparingTo("10.00")
        assertThat(createResult.responseHeaders.location?.path).isEqualTo("/api/payments/${created.persistedId}")

        val updatedTarget =
            client()
                .get()
                .uri("/api/users/{id}", target.persistedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<UserDto>()
                .responseBody!!
        assertThat(updatedTarget.balance).isEqualByComparingTo("10.00")

        val kittyResult =
            client()
                .get()
                .uri("/api/payments/kitty-balance")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<CashKittyBalanceDto>()
        assertThat(kittyResult.status.value()).isEqualTo(200)
        assertThat(kittyResult.responseBody!!.totalBalance).isEqualByComparingTo("10.00")
    }

    @Test
    fun `recording a cash deposit by a non-admin user returns 403 Forbidden and leaves balances unchanged`() {
        val sender = fixtureUser("lisa_lee")
        val recipient = fixtureUser("olivia_lee")

        val result =
            createPayment(
                newPayment(
                    recordedByUserId = sender.persistedId,
                    userId = recipient.persistedId,
                    amount = BigDecimal("10.00")
                )
            ).returnResult<ErrorResponse>()

        assertThat(result.status.value()).isEqualTo(403)
        assertThat(result.responseBody!!.errorCode).isEqualTo("ForbiddenException")

        val recipientAfter =
            client()
                .get()
                .uri("/api/users/{id}", recipient.persistedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<UserDto>()
                .responseBody!!
        assertThat(recipientAfter.balance).isEqualByComparingTo("0.00")
    }

    @Test
    fun `recording a cash deposit of zero or negative amount returns 400 Bad Request`() {
        val admin = fixtureUser("jane_doe")
        val target = fixtureUser("lisa_lee")

        for (invalidAmount in listOf(BigDecimal("0.00"), BigDecimal("-5.00"))) {
            val result =
                createPayment(
                    newPayment(
                        recordedByUserId = admin.persistedId,
                        userId = target.persistedId,
                        amount = invalidAmount
                    )
                ).returnResult<ErrorResponse>()

            assertThat(result.status.value()).isEqualTo(400)
            assertThat(result.responseBody!!.errorCode).isEqualTo("BadRequest")
        }

        val targetAfter =
            client()
                .get()
                .uri("/api/users/{id}", target.persistedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<UserDto>()
                .responseBody!!
        assertThat(targetAfter.balance).isEqualByComparingTo("0.00")
    }

    @Test
    fun `recording a cash deposit above 1000 euros returns 400 Bad Request`() {
        val admin = fixtureUser("jane_doe")
        val target = fixtureUser("lisa_lee")

        val result =
            createPayment(
                newPayment(
                    recordedByUserId = admin.persistedId,
                    userId = target.persistedId,
                    amount = BigDecimal("1000.01")
                )
            ).returnResult<ErrorResponse>()

        assertThat(result.status.value()).isEqualTo(400)
        assertThat(result.responseBody!!.errorCode).isEqualTo("BadRequest")

        val targetAfter =
            client()
                .get()
                .uri("/api/users/{id}", target.persistedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<UserDto>()
                .responseBody!!
        assertThat(targetAfter.balance).isEqualByComparingTo("0.00")
    }
}
