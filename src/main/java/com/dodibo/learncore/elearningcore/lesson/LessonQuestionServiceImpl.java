package com.dodibo.learncore.elearningcore.lesson;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.common.DisplayOrderHelper;
import com.dodibo.learncore.elearningcore.lesson.dto.AssignLessonQuestionRequest;
import com.dodibo.learncore.elearningcore.lesson.dto.FindLessonQuestionQuery;
import com.dodibo.learncore.elearningcore.lesson.dto.LessonQuestionResponse;
import com.dodibo.learncore.elearningcore.lesson.dto.UpdateLessonQuestionRequest;
import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.elearningcore.question.QuestionRepository;
import com.dodibo.learncore.exception.ResourceAlreadyExistsException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.tenant.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonQuestionServiceImpl implements LessonQuestionService {

    private final LessonQuestionRepository lessonQuestionRepository;
    private final LessonRepository lessonRepository;
    private final QuestionRepository questionRepository;
    private final LessonQuestionMapper lessonQuestionMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public LessonQuestionResponse assignQuestion(AssignLessonQuestionRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Lesson lesson = requireLesson(request.lessonUuid(), tenant.getId());
        Question question = requireQuestion(request.questionUuid(), tenant.getId());
        if (lessonQuestionRepository.findByLesson_IdAndQuestion_Id(lesson.getId(), question.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Question is already assigned to this lesson");
        }

        long currentCount = lessonQuestionRepository.countByLesson_Id(lesson.getId());
        int displayOrder = DisplayOrderHelper.resolveOrderForCreate(request.displayOrder(), currentCount);
        if (displayOrder <= currentCount) {
            lessonQuestionRepository.incrementOrdersFrom(lesson.getId(), displayOrder);
        }

        LessonQuestion assignment = LessonQuestion.builder()
                .lesson(lesson)
                .question(question)
                .displayOrder(displayOrder)
                .build();
        return lessonQuestionMapper.toResponse(lessonQuestionRepository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonQuestionResponse> getLessonQuestions(FindLessonQuestionQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<LessonQuestion> spec = LessonQuestionSpecification.fromQuery(query, tenant.getId());
        return lessonQuestionRepository.findAll(spec, query.toPageable())
                .map(lessonQuestionMapper::toResponse);
    }

    @Override
    @Transactional
    public LessonQuestionResponse updateAssignment(Long id, UpdateLessonQuestionRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        LessonQuestion assignment = requireAssignment(id, tenant.getId());
        Long lessonId = assignment.getLesson().getId();
        long currentCount = lessonQuestionRepository.countByLesson_Id(lessonId);
        int newOrder = DisplayOrderHelper.requireOrderForUpdate(request.displayOrder(), currentCount);
        Integer oldOrder = assignment.getDisplayOrder();

        if (oldOrder == null) {
            if (newOrder <= currentCount) {
                lessonQuestionRepository.incrementOrdersFrom(lessonId, newOrder);
            }
        } else if (!oldOrder.equals(newOrder)) {
            if (newOrder < oldOrder) {
                lessonQuestionRepository.shiftOrdersUp(lessonId, newOrder, oldOrder, id);
            } else {
                lessonQuestionRepository.shiftOrdersDown(lessonId, oldOrder, newOrder, id);
            }
        }

        assignment.setDisplayOrder(newOrder);
        return lessonQuestionMapper.toResponse(lessonQuestionRepository.save(assignment));
    }

    @Override
    @Transactional
    public void removeAssignment(Long id) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        LessonQuestion assignment = requireAssignment(id, tenant.getId());
        Long lessonId = assignment.getLesson().getId();
        Integer deletedOrder = assignment.getDisplayOrder();
        lessonQuestionRepository.delete(assignment);
        if (deletedOrder != null) {
            lessonQuestionRepository.decrementOrdersAbove(lessonId, deletedOrder);
        }
    }

    private LessonQuestion requireAssignment(Long id, Long tenantId) {
        return lessonQuestionRepository.findByIdAndLesson_Tenant_Id(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson question assignment not found: " + id));
    }

    private Lesson requireLesson(String lessonUuid, Long tenantId) {
        return lessonRepository.findByUuidAndTenant_IdAndIsDeletedFalse(lessonUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + lessonUuid));
    }

    private Question requireQuestion(String questionUuid, Long tenantId) {
        return questionRepository.findByUuidAndTenantIdAndIsDeletedFalse(questionUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionUuid));
    }
}
