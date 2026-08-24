package com.dodibo.learncore.elearningcore.bookmark;

import com.dodibo.learncore.elearningcore.bookmark.dto.BookmarkResponse;
import com.dodibo.learncore.elearningcore.bookmark.dto.CreateBookmarkRequest;
import com.dodibo.learncore.elearningcore.bookmark.dto.FindBookmarkQuery;
import org.springframework.data.domain.Page;

public interface BookmarkService {

    BookmarkResponse createBookmark(CreateBookmarkRequest request);

    Page<BookmarkResponse> getBookmarks(FindBookmarkQuery query);

    void deleteBookmark(String uuid);
}
