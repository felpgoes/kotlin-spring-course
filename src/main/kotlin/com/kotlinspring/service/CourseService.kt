package com.kotlinspring.service

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.entity.Course
import com.kotlinspring.exception.CourseNotFoundException
import com.kotlinspring.repository.CourseRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService (val courseRepository: CourseRepository) {

    companion object : KLogging()

    fun add(courseDTO: CourseDTO): CourseDTO {
        val course = courseDTO.let {
            Course(null, it.name, it.category)
        }

        courseRepository.save(course)

        logger.info("Saved course is: $course")

        return course.let {
            CourseDTO(it.id, it.name, it.category)
        }
    }

    fun retrieveAll(courseName: String?): List<CourseDTO> {
        val courses = courseName?.let {
            courseRepository.findCoursesByName(courseName)
        } ?: courseRepository.findAll()

        return courses.map { CourseDTO(it.id, it.name, it.category)}
    }

    fun update(courseId: Int, body: CourseDTO): CourseDTO {
        val existingCourse = courseRepository.findById(courseId)
        if (existingCourse.isEmpty) throw CourseNotFoundException("No course found with id: $courseId")

        return existingCourse.get()
            .let {
                it.name = body.name
                it.category = body.category
                courseRepository.save(it)
                CourseDTO(it.id, it.name, it.category)
            }

//        Código do curso
//        return if (existingCourse.isPresent) {
//            existingCourse.get()
//                .let {
//                    it.name = body.name
//                    it.category = body.category
//                    courseRepository.save(it)
//                    CourseDTO(it.id, it.name, it.category)
//                }
//        } else {
//            throw CourseNotFoundException("No course found with id: $courseId")
//        }
    }

    fun delete(courseId: Int) {
        val existingCourse = courseRepository.findById(courseId)
        if (existingCourse.isEmpty) throw CourseNotFoundException("No course found with id: $courseId")

        courseRepository.deleteById(courseId)
    }

}
