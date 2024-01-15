package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import util.courseEntityList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Roda o servidor de test em uma porta aleatoria
@ActiveProfiles("test") // usa o profiler de test (application-test.yml)
@AutoConfigureWebTestClient // Ao inves de utilizar um server HTTP, as req batem diretamente na app
class CourseControllerIntgTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        val courses = courseEntityList()
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {
        val courseDTO = CourseDTO(null, "Spring Legal", "Felipe")

        val savedCourseDTO = webTestClient
            .post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange() // Request acontece aqui
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertNotNull(savedCourseDTO!!.id)
    }

    @Test
    fun retrieveAllCourses() {
        val savedCourseDTO = webTestClient
            .get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(savedCourseDTO!!.size, 3)
    }

    @Test
    fun retrieveAllCoursesByName() {

        val uri = UriComponentsBuilder
            .fromUriString("/v1/courses")
            .queryParam("course_name", "SpringBoot")
            .toUriString()

        val savedCourseDTO = webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(savedCourseDTO!!.size, 2)
    }

    @Test
    fun updateCourses() {
        val course = Course(null,"Build RestFul APis using SpringBoot and Kotlin", "Development")
        courseRepository.save(course)

        val courseDTO = CourseDTO(null,"Build RestFul APis using SpringBoot and Kotlin1", "Development")

        val updatedCourseDTO = webTestClient
            .put()
            .uri("/v1/courses/{courseId}", course.id)
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
        val course = Course(null,"Build RestFul APis using SpringBoot and Kotlin", "Development")
        courseRepository.save(course)

        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", course.id)
            .exchange() // Request acontece aqui
            .expectStatus().isNoContent
    }
}