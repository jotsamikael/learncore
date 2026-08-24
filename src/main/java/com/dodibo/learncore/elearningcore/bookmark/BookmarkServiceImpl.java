package com.dodibo.learncore.elearningcore.bookmark;

import com.dodibo.learncore.common.tenant.TenantMemberResolver;
import com.dodibo.learncore.elearningcore.bookmark.dto.BookmarkResponse;
import com.dodibo.learncore.elearningcore.bookmark.dto.CreateBookmarkRequest;
import com.dodibo.learncore.elearningcore.bookmark.dto.FindBookmarkQuery;
import com.dodibo.learncore.elearningcore.question.Question;
import com.dodibo.learncore.elearningcore.question.QuestionRepository;
import com.dodibo.learncore.exception.ResourceAlreadyExistsException;
import com.dodibo.learncore.exception.ResourceNotFoundException;
import com.dodibo.learncore.tenant.Tenant;
import com.dodibo.learncore.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final QuestionRepository questionRepository;
    private final BookmarkMapper bookmarkMapper;
    private final TenantMemberResolver tenantMemberResolver;

    @Override
    @Transactional
    public BookmarkResponse createBookmark(CreateBookmarkRequest request) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        User user = tenantMemberResolver.requireCurrentUser();
        Question question = questionRepository.findByUuidAndTenantId(request.questionUuid(), tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + request.questionUuid()));
        if (bookmarkRepository.findByTenant_IdAndUser_IdAndQuestion_Id(tenant.getId(), user.getId(), question.getId()).isPresent()) {
            throw new ResourceAlreadyExistsException("Question is already bookmarked");
        }
        Bookmark bookmark = bookmarkMapper.toEntity(tenant, user, question);
        return bookmarkMapper.toResponse(bookmarkRepository.save(bookmark));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getBookmarks(FindBookmarkQuery query) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        User user = tenantMemberResolver.requireCurrentUser();
        Specification<Bookmark> spec = BookmarkSpecification.fromQuery(query, tenant.getId(), user.getId());
        return bookmarkRepository.findAll(spec, query.toPageable())
                .map(bookmarkMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteBookmark(String uuid) {
        Tenant tenant = tenantMemberResolver.requireCallerTenant();
        User user = tenantMemberResolver.requireCurrentUser();
        Bookmark bookmark = bookmarkRepository.findByUuidAndTenant_IdAndUser_Id(uuid, tenant.getId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found: " + uuid));
        bookmarkRepository.delete(bookmark);
    }
}
