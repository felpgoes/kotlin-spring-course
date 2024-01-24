package util

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.entity.Instructor

fun courseEntityList(instructor: Instructor) = listOf(
    Course(null,"Build RestFul APis using SpringBoot and Kotlin", "Development", instructor),
    Course(null,"Build Reactive Microservices using Spring WebFlux/SpringBoot", "Development", instructor),
    Course(null,"Wiremock for Java Developers", "Development", instructor)
)

fun instructorEntity(name: String = "Felipe Goes") = Instructor(1, name)

fun courseDTO(
    id: Int? = null,
    name: String = "Build RestFul APis using Spring Boot and Kotlin",
    category: String = "Felipe Goes",
    instructor: Int = instructorEntity().id!!
) = CourseDTO(
    id,
    name,
    category,
    instructor
)

fun instructorDTO(
    id: Int? = null,
    name: String = "Felipe Goes"
) = InstructorDTO(
    id,
    name
)