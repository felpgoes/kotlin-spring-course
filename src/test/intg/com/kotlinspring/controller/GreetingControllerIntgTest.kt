package com.kotlinspring.controller

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
class GreetingControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun retrieveGreeting() {
        val name = "Felipe"

        val result = webTestClient
            .get()
            .uri("/v1/greetings/{name}", name)
            .exchange() // Request acontece aqui
            .expectStatus().is2xxSuccessful
            .expectBody(String::class.java)
            .returnResult()

        Assertions.assertEquals("$name, Hello from test profile!", result.responseBody)
    }
}