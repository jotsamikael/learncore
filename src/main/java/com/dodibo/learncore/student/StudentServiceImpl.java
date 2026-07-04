package com.dodibo.learncore.student;

import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.exception.TenantAccessDeniedException;
import com.dodibo.learncore.security.SecurityUtils;
import com.dodibo.learncore.staff.Staff;
import com.dodibo.learncore.student.dto.FindStudentsQuery;
import com.dodibo.learncore.student.dto.StudentDetailResponse;
import com.dodibo.learncore.student.dto.StudentResponse;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.tenant.TenantRepository;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudents(FindStudentsQuery query) {
        Long tenantId = resolveTenantId(query.getTenantUuid());
        Specification<Student> spec = StudentSpecification.fromQuery(query)
                .and(StudentSpecification.hasTenantId(tenantId));

        return studentRepository.findAll(spec, query.toPageable())
                .map(studentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDetailResponse> getStudentDetail(String uuid) {
        return studentRepository.findByUuid(uuid)
                .filter(this::canAccessStudent)
                .map(studentMapper::toDetailResponse);
    }

    @Override
    @Transactional
    public void activateDeactivate(String uuid) {
        Student student = studentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + uuid));

        if (!canAccessStudent(student)) {
            throw new TenantAccessDeniedException("Cannot access student outside your tenant");
        }

        student.setAccountLocked(!student.isAccountLocked());
        studentRepository.save(student);
    }
    private Long resolveTenantId(String requestedTenantUuid) {
        User user = SecurityUtils.getCurrentUser();

        if (user instanceof Staff staff && staff.isTenantStaff()) {
            Long tenantId = staff.getTenantId();
            if (requestedTenantUuid != null && !requestedTenantUuid.isBlank()) {
                String actualTenantUuid = tenantRepository.findById(tenantId)
                        .map(Tenant::getUuid)
                        .orElse(null);
                if (!requestedTenantUuid.equals(actualTenantUuid)) {
                    throw new TenantAccessDeniedException("Cannot access students outside your tenant");
                }
            }
            return tenantId;
        }

        if (requestedTenantUuid == null || requestedTenantUuid.isBlank()) {
            throw new OperationNotPermittedException("tenantUuid is required");
        }

        return tenantRepository.findByUuid(requestedTenantUuid)
                .map(Tenant::getId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + requestedTenantUuid));
    }

    private boolean canAccessStudent(Student student) {
        User user = SecurityUtils.getCurrentUser();

        if (user instanceof Staff staff && staff.isTenantStaff()) {
            return student.getTenantId() != null && student.getTenantId().equals(staff.getTenantId());
        }

        return user.isPlatformUser();
    }
}
