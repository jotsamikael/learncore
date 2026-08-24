package com.dodibo.learncore.elearningcore.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, JpaSpecificationExecutor<Bookmark> {

    Optional<Bookmark> findByTenant_IdAndUser_IdAndQuestion_Id(Long tenantId, Long userId, Long questionId);

    Optional<Bookmark> findByUuidAndTenant_IdAndUser_Id(String uuid, Long tenantId, Long userId);
}
