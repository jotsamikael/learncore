package com.dodibo.learncore.student;

import com.dodibo.learncore.student.dto.FindStudentsQuery;
import com.dodibo.learncore.student.dto.StudentDetailResponse;
import com.dodibo.learncore.student.dto.StudentResponse;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface StudentService {

    Page<StudentResponse> getStudents(FindStudentsQuery query);

    Optional<StudentDetailResponse> getStudentDetail(String uuid);

    void activateDeactivate(String uuid);

}
