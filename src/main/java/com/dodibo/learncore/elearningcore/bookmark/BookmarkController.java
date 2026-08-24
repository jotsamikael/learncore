package com.dodibo.learncore.elearningcore.bookmark;

import org.springdoc.core.annotations.ParameterObject;
import com.dodibo.learncore.elearningcore.bookmark.dto.BookmarkResponse;
import com.dodibo.learncore.elearningcore.bookmark.dto.CreateBookmarkRequest;
import com.dodibo.learncore.elearningcore.bookmark.dto.FindBookmarkQuery;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dodibo.learncore.permission.PermissionCodes.TENANT_BOOKMARK_CREATE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_BOOKMARK_DELETE;
import static com.dodibo.learncore.permission.PermissionCodes.TENANT_BOOKMARK_READ;

@RestController
@RequestMapping("bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authz.can('" + TENANT_BOOKMARK_CREATE + "')")
    public ResponseEntity<BookmarkResponse> createBookmark(@RequestBody @Valid CreateBookmarkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmarkService.createBookmark(request));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authz.can('" + TENANT_BOOKMARK_READ + "')")
    public ResponseEntity<Page<BookmarkResponse>> getBookmarks(@ParameterObject FindBookmarkQuery query) {
        return ResponseEntity.ok(bookmarkService.getBookmarks(query));
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authz.can('" + TENANT_BOOKMARK_DELETE + "')")
    public ResponseEntity<Void> deleteBookmark(@PathVariable String uuid) {
        bookmarkService.deleteBookmark(uuid);
        return ResponseEntity.noContent().build();
    }
}
