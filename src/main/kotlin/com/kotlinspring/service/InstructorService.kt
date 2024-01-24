package com.kotlinspring.service

import com.kotlinspring.dto.InstructorDTO
import com.kotlinspring.entity.Instructor
import com.kotlinspring.repository.InstructorRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class InstructorService (private val instructorRepository: InstructorRepository) {
    fun create(instructorDTO: InstructorDTO): InstructorDTO {
        val instructor = Instructor(null, instructorDTO.name)

        instructorRepository.save(instructor)

        return instructor.let { InstructorDTO(it.id, it.name) }
    }

    fun findByInstructorId(instructorId: Int): Optional<Instructor> = instructorRepository.findById(instructorId)

}
