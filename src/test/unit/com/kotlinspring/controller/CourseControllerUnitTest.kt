package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.service.CourseService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import util.courseDTO

@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMock: CourseService

    @Test
    fun addCourse() {
        val courseDTO = CourseDTO(null, "Build RestFul APis using Spring Boot and Kotlin", "Felipe Goes")

        every { courseServiceMock.add(any()) } returns courseDTO(1)

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange() // Request acontece aqui
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(savedCourseDTO!!.id, 1)
    }

    @Test
    fun addCourseValidation() {
        val courseDTO = CourseDTO(null, "", "")

        every { courseServiceMock.add(any()) } returns courseDTO(1)

        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange() // Request acontece aqui
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("courseDTO.category must not be blank, courseDTO.name must not be blank", response)
    }

    @Test
    fun addCourseRuntimeException() {
        val courseDTO = CourseDTO(null, "Build RestFul APis using Spring Boot and Kotlin", "Felipe Goes")

        val errorMessage = "Unexpected Error occurred"
        every { courseServiceMock.add(any()) } throws RuntimeException(errorMessage)

        val response = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(errorMessage, response)
    }

    @Test
    fun retrieveAllCourses() {
        every { courseServiceMock.retrieveAll(any()) }
            .returnsMany(
                listOf(
                    courseDTO(1, "Curso de Kotlin"),
                    courseDTO(2, "Curso de JavaScript")
                )
            )

        val savedCourseDTO = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(savedCourseDTO!!.size, 2)
        Assertions.assertEquals(savedCourseDTO[0].name, "Curso de Kotlin")
        Assertions.assertEquals(savedCourseDTO[1].name, "Curso de JavaScript")
    }

    @Test
    fun updateCourses() {
        every { courseServiceMock.update(any(), any()) } returns courseDTO(100, "Build RestFul APis using SpringBoot and Kotlin1")

        val courseDTO = CourseDTO(null,"Build RestFul APis using SpringBoot and Kotlin1", "Development")

        val updatedCourseDTO = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", 100)
            .bodyValue(courseDTO)
            .exchange() // Request acontece aqui
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("Build RestFul APis using SpringBoot and Kotlin1", updatedCourseDTO!!.name)
    }

    @Test
    fun deleteCourses() {
        every { courseServiceMock.delete(any()) } just runs // Apenas roda o mock, já que o retorno é vazio

        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", "199")
            .exchange() // Request acontece aqui
            .expectStatus().isNoContent
    }

}