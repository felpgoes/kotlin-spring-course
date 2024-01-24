package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.repository.CourseRepository
import com.kotlinspring.repository.InstructorRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import util.PostgresSQLContainerInitializer
import util.courseEntityList
import util.instructorEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Roda o servidor de test em uma porta aleatoria
@ActiveProfiles("postgres") // usa o profiler de test (application-test.yml)
@AutoConfigureWebTestClient // Ao inves de utilizar um server HTTP, as req batem diretamente na app
class CourseControllerIntgTest : PostgresSQLContainerInitializer() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        instructorRepository.deleteAll()

        val instructor = instructorEntity()
        instructorRepository.save(instructor)

        val courses = courseEntityList(instructor)
        courseRepository.saveAll(courses)
    }

    @Test
    fun addCourse() {
        val instructor = instructorRepository.findAll().first()
        Assertions.assertNotNull(instructor.id)

        val courseDTO = CourseDTO(null, "Spring Legal", "Felipe", instructor.id!!)

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
        val instructor = instructorRepository.findAll().first()
        Assertions.assertNotNull(instructor.id)

        val course = Course(null,"Build RestFul APis using SpringBoot and Kotlin", "Development", instructor)
        courseRepository.save(course)

        val courseDTO = CourseDTO(null,"Build RestFul APis using SpringBoot and Kotlin1", "Development", instructor.id!!)

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
        val instructor = instructorRepository.findAll().first()
        Assertions.assertNotNull(instructor.id)

        val course = Course(null,"Build RestFul APis using SpringBoot and Kotlin", "Development", instructor)
        courseRepository.save(course)

        webTestClient
            .delete()
            .uri("/v1/courses/{courseId}", course.id)
            .exchange() // Request acontece aqui
            .expectStatus().isNoContent
    }
}