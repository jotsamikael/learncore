package com.dodibo.learncore.elearningcore.daily_quiz;

import com.dodibo.learncore.common.tenant.TenantStaffResolver;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.AssignDailyQuizQuestionRequest;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.DailyQuizQuestionResponse;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.FindDailyQuizQuestionQuery;
import com.dodibo.learncore.elearningcore.daily_quiz.dto.UpdateDailyQuizQuestionRequest;
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
public class DailyQuizQuestionServiceImpl implements DailyQuizQuestionService {

    private final DailyQuizQuestionRepository dailyQuizQuestionRepository;
    private final DailyQuizRepository dailyQuizRepository;
    private final QuestionRepository questionRepository;
    private final DailyQuizQuestionMapper dailyQuizQuestionMapper;
    private final TenantStaffResolver tenantStaffResolver;

    @Override
    @Transactional
    public DailyQuizQuestionResponse assignQuestion(AssignDailyQuizQuestionRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        DailyQuiz dailyQuiz = requireDailyQuiz(request.dailyQuizUuid(), tenant.getId());
        Question question = requireQuestion(request.questionUuid(), tenant.getId());
        if (dailyQuizQuestionRepository.findByDailyQuiz_IdAndQuestion_Id(dailyQuiz.getId(), question.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Question is already assigned to this daily quiz");
        }
        DailyQuizQuestion assignment = DailyQuizQuestion.builder()
                .dailyQuiz(dailyQuiz)
                .question(question)
                .displayOrder(request.displayOrder())
                .build();
        return dailyQuizQuestionMapper.toResponse(dailyQuizQuestionRepository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DailyQuizQuestionResponse> getDailyQuizQuestions(FindDailyQuizQuestionQuery query) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        Specification<DailyQuizQuestion> spec = DailyQuizQuestionSpecification.fromQuery(query, tenant.getId());
        return dailyQuizQuestionRepository.findAll(spec, query.toPageable())
                .map(dailyQuizQuestionMapper::toResponse);
    }

    @Override
    @Transactional
    public DailyQuizQuestionResponse updateAssignment(Long id, UpdateDailyQuizQuestionRequest request) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        DailyQuizQuestion assignment = requireAssignment(id, tenant.getId());
        assignment.setDisplayOrder(request.displayOrder());
        return dailyQuizQuestionMapper.toResponse(dailyQuizQuestionRepository.save(assignment));
    }

    @Override
    @Transactional
    public void removeAssignment(Long id) {
        Tenant tenant = tenantStaffResolver.requireCallerTenant();
        DailyQuizQuestion assignment = requireAssignment(id, tenant.getId());
        dailyQuizQuestionRepository.delete(assignment);
    }

    private DailyQuizQuestion requireAssignment(Long id, Long tenantId) {
        return dailyQuizQuestionRepository.findByIdAndDailyQuiz_Tenant_Id(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily quiz question assignment not found: " + id));
    }

    private DailyQuiz requireDailyQuiz(String dailyQuizUuid, Long tenantId) {
        return dailyQuizRepository.findByUuidAndTenant_IdAndIsDeletedFalse(dailyQuizUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily quiz not found: " + dailyQuizUuid));
    }

    private Question requireQuestion(String questionUuid, Long tenantId) {
        return questionRepository.findByUuidAndTenantIdAndIsDeletedFalse(questionUuid, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionUuid));
    }
}
