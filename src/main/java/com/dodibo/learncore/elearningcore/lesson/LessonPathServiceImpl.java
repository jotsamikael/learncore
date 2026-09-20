package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.common.tenant.TenantMemberResolver;
import com.dodibo.learncore.elearningcore.category.Category;
import com.dodibo.learncore.elearningcore.category.CategoryRepository;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonPathItemResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonPathResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonPathStatus;
import com.dodibo.learncore.elearningcore.lesson.dto.StudentLessonDetailResponse;
import com.dodibo.learncore.exception.OperationNotPermittedException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.student.Student;
import com.dodibo.learncore.subscription.SubscriptionType;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonPathServiceImpl implements LessonPathService {

    private final LessonRepository lessonRepository;
    private final LessonQuestionRepository lessonQuestionRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final CategoryRepository categoryRepository;
    private final TenantMemberResolver tenantMemberResolver;

    @Override
    @Transactional(readOnly = true)
    public LessonPathResponse getCategoryPath(String categoryUuid) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        Student student = requireStudent();
        Category category = requireCategory(categoryUuid, tenant.getId());

        List<Lesson> lessons =
                lessonRepository.findByCategory_IdAndIsDeletedFalseOrderByPositionAsc(category.getId());
        Set<Long> completedLessonIds = userLessonProgressRepository.findCompletedLessonIdsByUserAndCategory(
                student.getId(),
                category.getId()
        );

        List<LessonPathItemResponse> items = new ArrayList<>();
        for (Lesson lesson : lessons) {
            LessonPathStatus status = resolveStatus(lesson, completedLessonIds, student);
            int exerciseCount = (int) lessonQuestionRepository.countByLesson_Id(lesson.getId());
            items.add(new LessonPathItemResponse(
                    lesson.getUuid(),
                    lesson.getTitle(),
                    lesson.getPosition(),
                    lesson.getCompletionTime(),
                    Boolean.TRUE.equals(lesson.getIsPremium()),
                    exerciseCount,
                    status
            ));
        }

        return new LessonPathResponse(category.getUuid(), category.getName(), items);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentLessonDetailResponse getLessonForStudent(String lessonUuid) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        Student student = requireStudent();
        Lesson lesson = requireLesson(lessonUuid, tenant.getId());

        Set<Long> completedLessonIds = userLessonProgressRepository.findCompletedLessonIdsByUserAndCategory(
                student.getId(),
                lesson.getCategory().getId()
        );
        LessonPathStatus status = resolveStatus(lesson, completedLessonIds, student);
        if (status == LessonPathStatus.LOCKED) {
            throw new OperationNotPermittedException(
                    "This lesson is locked. Complete the previous lesson in the path first."
            );
        }

        List<String> exerciseUuids = lessonQuestionRepository.findQuestionUuidsByLessonId(lesson.getId());
        return new StudentLessonDetailResponse(
                lesson.getUuid(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getPosition(),
                lesson.getCompletionTime(),
                Boolean.TRUE.equals(lesson.getIsPremium()),
                status,
                exerciseUuids
        );
    }

    @Override
    @Transactional
    public StudentLessonDetailResponse completeLesson(String lessonUuid) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        Student student = requireStudent();
        Lesson lesson = requireLesson(lessonUuid, tenant.getId());

        Set<Long> completedLessonIds = userLessonProgressRepository.findCompletedLessonIdsByUserAndCategory(
                student.getId(),
                lesson.getCategory().getId()
        );
        LessonPathStatus status = resolveStatus(lesson, completedLessonIds, student);
        if (status == LessonPathStatus.LOCKED) {
            throw new OperationNotPermittedException(
                    "You cannot complete a locked lesson."
            );
        }

        if (!userLessonProgressRepository.existsByUser_IdAndLesson_Id(student.getId(), lesson.getId())) {
            userLessonProgressRepository.save(UserLessonProgress.builder()
                    .user(student)
                    .lesson(lesson)
                    .completedAt(LocalDateTime.now())
                    .build());
        }

        List<String> exerciseUuids = lessonQuestionRepository.findQuestionUuidsByLessonId(lesson.getId());
        return new StudentLessonDetailResponse(
                lesson.getUuid(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getPosition(),
                lesson.getCompletionTime(),
                Boolean.TRUE.equals(lesson.getIsPremium()),
                LessonPathStatus.COMPLETED,
                exerciseUuids
        );
    }

    private LessonPathStatus resolveStatus(
            Lesson lesson,
            Set<Long> completedLessonIds,
            Student student
    ) {
        if (completedLessonIds.contains(lesson.getId())) {
            return LessonPathStatus.COMPLETED;
        }
        if (Boolean.TRUE.equals(lesson.getIsPremium())
                && student.getSubscriptionType() == SubscriptionType.FREE) {
            return LessonPathStatus.LOCKED;
        }
        if (lesson.getPosition() <= 1) {
            return LessonPathStatus.AVAILABLE;
        }
        Long categoryId = lesson.getCategory().getId();
        return lessonRepository
                .findByCategory_IdAndPositionAndIsDeletedFalse(categoryId, lesson.getPosition() - 1)
                .map(previous -> completedLessonIds.contains(previous.getId())
                        ? LessonPathStatus.AVAILABLE
                        : LessonPathStatus.LOCKED)
                .orElse(LessonPathStatus.AVAILABLE);
    }

    private Student requireStudent() {
        User user = tenantMemberResolver.requireCurrentUser();
        if (!(user instanceof Student student)) {
            throw new OperationNotPermittedException("Only students can access the lesson path.");
        }
        return student;
    }

    private Category requireCategory(String categoryUuid, Long tenantId) {
        if (!StringUtils.hasText(categoryUuid)) {
            throw new OperationNotPermittedException("categoryUuid is required.");
        }
        return categoryRepository.findByUuidAndTenantIdAndIsDeletedFalse(categoryUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryUuid));
    }

    private Lesson requireLesson(String lessonUuid, Long tenantId) {
        return lessonRepository.findByUuidAndTenant_IdAndIsDeletedFalse(lessonUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + lessonUuid));
    }
}
