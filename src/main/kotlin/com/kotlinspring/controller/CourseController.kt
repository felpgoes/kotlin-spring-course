package com.kotlinspring.controller

import com.kotlinspring.dto.CourseDTO
import com.kotlinspring.service.CourseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/v1/courses")
@Validated
class CourseController (val courseService: CourseService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody @Valid body: CourseDTO): CourseDTO = courseService.add(body)

    @GetMapping
    fun retrieveAll(@RequestParam("course_name") courseName: String?): List<CourseDTO> = courseService.retrieveAll(courseName)

    @PutMapping("/{course_id}")
    fun update(@RequestBody body: CourseDTO, @PathVariable("course_id") courseId: Int) = courseService.update(courseId, body)

    @DeleteMapping("/{course_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("course_id") courseId: Int) = courseService.delete(courseId)

}