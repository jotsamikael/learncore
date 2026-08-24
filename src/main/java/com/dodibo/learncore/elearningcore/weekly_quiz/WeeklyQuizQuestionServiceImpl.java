package com.dodibo.learncore.elearningcore.weekly_quiz;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.elearningcore.question.QuestionRepository;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.AssignWeeklyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.FindWeeklyQuizQuestionQuery;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.UpdateWeeklyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.weekly_quiz.dto.WeeklyQuizQuestionResponse;
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
public class WeeklyQuizQuestionServiceImpl implements WeeklyQuizQuestionService {

    private final WeeklyQuizQuestionRepository weeklyQuizQuestionRepository;
    private final WeeklyQuizRepository weeklyQuizRepository;
    private final QuestionRepository questionRepository;
    private final WeeklyQuizQuestionMapper weeklyQuizQuestionMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public WeeklyQuizQuestionResponse assignQuestion(AssignWeeklyQuizQuestionRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        WeeklyQuiz weeklyQuiz = requireWeeklyQuiz(request.weeklyQuizUuid(), tenant.getId());
        Question question = requireQuestion(request.questionUuid(), tenant.getId());
        if (weeklyQuizQuestionRepository.findByWeeklyQuiz_IdAndQuestion_Id(weeklyQuiz.getId(), question.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Question is already assigned to this weekly quiz");
        }
        WeeklyQuizQuestion assignment = WeeklyQuizQuestion.builder()
                .weeklyQuiz(weeklyQuiz)
                .question(question)
                .displayOrder(request.displayOrder())
                .build();
        return weeklyQuizQuestionMapper.toResponse(weeklyQuizQuestionRepository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WeeklyQuizQuestionResponse> getWeeklyQuizQuestions(FindWeeklyQuizQuestionQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<WeeklyQuizQuestion> spec = WeeklyQuizQuestionSpecification.fromQuery(query, tenant.getId());
        return weeklyQuizQuestionRepository.findAll(spec, query.toPageable())
                .map(weeklyQuizQuestionMapper::toResponse);
    }

    @Override
    @Transactional
    public WeeklyQuizQuestionResponse updateAssignment(Long id, UpdateWeeklyQuizQuestionRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        WeeklyQuizQuestion assignment = requireAssignment(id, tenant.getId());
        assignment.setDisplayOrder(request.displayOrder());
        return weeklyQuizQuestionMapper.toResponse(weeklyQuizQuestionRepository.save(assignment));
    }

    @Override
    @Transactional
    public void removeAssignment(Long id) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        WeeklyQuizQuestion assignment = requireAssignment(id, tenant.getId());
        weeklyQuizQuestionRepository.delete(assignment);
    }

    private WeeklyQuizQuestion requireAssignment(Long id, Long tenantId) {
        return weeklyQuizQuestionRepository.findByIdAndWeeklyQuiz_Tenant_Id(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Weekly quiz question assignment not found: " + id));
    }

    private WeeklyQuiz requireWeeklyQuiz(String weeklyQuizUuid, Long tenantId) {
        return weeklyQuizRepository.findByUuidAndTenant_IdAndIsDeletedFalse(weeklyQuizUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Weekly quiz not found: " + weeklyQuizUuid));
    }

    private Question requireQuestion(String questionUuid, Long tenantId) {
        return questionRepository.findByUuidAndTenantIdAndIsDeletedFalse(questionUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionUuid));
    }
}
