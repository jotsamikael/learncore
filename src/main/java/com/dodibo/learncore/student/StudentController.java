package com.dodibo.learncore.student;

import com.dodibo.learncore.student.dto.FindStudentsQuery;
import com.dodibo.learncore.student.dto.StudentDetailResponse;
import com.dodibo.learncore.student.dto.StudentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
@Tag(name = "Students")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @PreAuthorize("@authz.can('tenant.student.read')")
    public ResponseEntity<Page<StudentResponse>> getStudents(@ModelAttribute FindStudentsQuery query) {
        return ResponseEntity.ok(studentService.getStudents(query));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("@authz.can('tenant.student.read')")
    public ResponseEntity<Optional<StudentDetailResponse>> getStudentDetails(@PathVariable String uuid) {
        return ResponseEntity.ok(studentService.getStudentDetail(uuid));
    }

    @PatchMapping("/{uuid}/status")
    @PreAuthorize("@authz.can('tenant.student.update')")
    public ResponseEntity<Void> activateDeactivate(@PathVariable String uuid) {
        studentService.activateDeactivate(uuid);
        return ResponseEntity.ok().build();
    }
}
