package com.kotlinspring.controller

import com.kotlinspring.dto.InstructorDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Roda o servidor de test em uma porta aleatoria
@ActiveProfiles("test") // usa o profiler de test (application-test.yml)
@AutoConfigureWebTestClient // Ao inves de utilizar um server HTTP, as req batem diretamente na app
class InstructorControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun addInstructor() {
        val instructorDTO = InstructorDTO(null, "Felipe Goes")

        val savedInstructorDTO = webTestClient
            .post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange() // Request acontece aqui
            .expectStatus().isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertNotNull(savedInstructorDTO!!.id)
        Assertions.assertEquals(savedInstructorDTO.id, 1)
    }

}