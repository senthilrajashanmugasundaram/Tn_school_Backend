package com.tnschool.sms.modules.cms.repository;

import com.tnschool.sms.modules.cms.model.CmsContentEntity;
import com.tnschool.sms.modules.cms.model.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface CmsContentRepository extends JpaRepository<CmsContentEntity, String> {
    List<CmsContentEntity> findAllByPublishedTrueAndContentTypeOrderByPublishedAtDesc(ContentType contentType);
    List<CmsContentEntity> findAllByPublishedTrueOrderByPublishedAtDesc();
    List<CmsContentEntity> findAllByPublishedTrueAndExpiresAtAfterOrderByPublishedAtDesc(OffsetDateTime now);
}
