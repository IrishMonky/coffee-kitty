package de.seuhd.campuscoffee.tests.system

import de.seuhd.campuscoffee.api.dtos.ExpenseDto
import de.seuhd.campuscoffee.api.dtos.UserDto
import de.seuhd.campuscoffee.api.exceptions.ErrorResponse
import de.seuhd.campuscoffee.domain.model.persistedId
import de.seuhd.campuscoffee.tests.SystemTestUtils.client
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.returnResult
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * System tests for listing and recording coffee bean expenses through `/api/expenses`.
 */
class ExpenseSystemTests : AbstractSystemTest() {
    /**
     * Returns a valid request body for recording a coffee bean expense for [userId].
     */
    private fun newExpense(
        userId: UUID,
        beanName: String = "Espresso Roast",
        weightGrams: Int = 500,
        amount: BigDecimal = BigDecimal("14.50"),
        note: String? = "Institutsküche"
    ): Map<String, Any> =
        buildMap {
            put("userId", userId.toString())
            put("beanName", beanName)
            put("weightGrams", weightGrams)
            put("amount", amount)
            if (note != null) {
                put("note", note)
            }
        }

    private fun createExpense(
        body: Map<String, Any>,
        uri: String = "/api/expenses"
    ) = client()
        .post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .exchange()

    @Test
    fun `listing expenses initially returns 200 OK with an empty list`() {
        val result =
            client()
                .get()
                .uri("/api/expenses")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<Array<ExpenseDto>>()

        assertThat(result.status.value()).isEqualTo(200)
        assertThat(result.responseBody).isEmpty()
    }

    @Test
    fun `recording an expense returns 201 Created, credits the user balance, and lists the expense`() {
        val lisa = fixtureUser("lisa_lee")

        val createResult = createExpense(newExpense(userId = lisa.persistedId)).returnResult<ExpenseDto>()

        assertThat(createResult.status.value()).isEqualTo(201)
        val created = createResult.responseBody!!
        assertThat(created.userId).isEqualTo(lisa.persistedId)
        assertThat(created.loginName).isEqualTo("lisa_lee")
        assertThat(created.beanName).isEqualTo("Espresso Roast")
        assertThat(created.weightGrams).isEqualTo(500)
        assertThat(created.amount).isEqualByComparingTo("14.50")
        assertThat(created.note).isEqualTo("Institutsküche")
        assertThat(created.createdAt).isCloseTo(LocalDateTime.now(ZoneOffset.UTC), within(1, ChronoUnit.MINUTES))
        assertThat(createResult.responseHeaders.location?.path).isEqualTo("/api/expenses/${created.persistedId}")

        val updatedUser =
            client()
                .get()
                .uri("/api/users/{id}", lisa.persistedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<UserDto>()
                .responseBody!!
        assertThat(updatedUser.balance).isEqualByComparingTo("14.50")

        val listResult =
            client()
                .get()
                .uri("/api/expenses")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult<Array<ExpenseDto>>()
        assertThat(listResult.status.value()).isEqualTo(200)
        assertThat(listResult.responseBody!!.map { it.id }).containsExactly(created.id)
    }

    @Test
    fun `recording an expense for an unknown user returns 404 Not Found`() {
        val result = createExpense(newExpense(userId = UUID.randomUUID())).returnResult<ErrorResponse>()

        assertThat(result.status.value()).isEqualTo(404)
        assertThat(result.responseBody!!.errorCode).isEqualTo("NotFoundException")
    }

    @Test
    fun `recording an expense with a blank bean name returns 400 Bad Request`() {
        val lisa = fixtureUser("lisa_lee")

        val result =
            createExpense(
                newExpense(userId = lisa.persistedId, beanName = "   ")
            ).returnResult<ErrorResponse>()

        assertThat(result.status.value()).isEqualTo(400)
        assertThat(result.responseBody!!.errorCode).isEqualTo("BadRequest")
    }

    @Test
    fun `recording an expense with weight below 100 grams returns 400 Bad Request`() {
        val lisa = fixtureUser("lisa_lee")

        val result =
            createExpense(
                newExpense(userId = lisa.persistedId, weightGrams = 99)
            ).returnResult<ErrorResponse>()

        assertThat(result.status.value()).isEqualTo(400)
        assertThat(result.responseBody!!.errorCode).isEqualTo("BadRequest")
    }

    @Test
    fun `recording an expense with amount above 1000 euros returns 400 Bad Request`() {
        val lisa = fixtureUser("lisa_lee")

        val result =
            createExpense(newExpense(userId = lisa.persistedId, amount = BigDecimal("1000.01")))
                .returnResult<ErrorResponse>()

        assertThat(result.status.value()).isEqualTo(400)
        assertThat(result.responseBody!!.errorCode).isEqualTo("BadRequest")
    }
}
