package com.tnschool.sms.modules.cms.service;

import com.tnschool.sms.modules.cms.dto.CmsContentResponse;
import com.tnschool.sms.modules.cms.dto.CreateCmsContentRequest;
import com.tnschool.sms.modules.cms.dto.UpdateCmsContentRequest;
import com.tnschool.sms.modules.cms.model.CmsContentEntity;
import com.tnschool.sms.modules.cms.model.ContentType;
import com.tnschool.sms.modules.cms.repository.CmsContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CmsService {

    private final CmsContentRepository cmsContentRepository;

    public CmsService(CmsContentRepository cmsContentRepository) {
        this.cmsContentRepository = cmsContentRepository;
    }

    @Transactional(readOnly = true)
    public List<CmsContentResponse> getPublished(String type) {
        List<CmsContentEntity> items;
        if (type != null && !type.isBlank()) {
            items = cmsContentRepository.findAllByPublishedTrueAndContentTypeOrderByPublishedAtDesc(ContentType.valueOf(type.toUpperCase()));
        } else {
            items = cmsContentRepository.findAllByPublishedTrueOrderByPublishedAtDesc();
        }

        OffsetDateTime now = OffsetDateTime.now();
        return items.stream()
                .filter(item -> item.getExpiresAt() == null || item.getExpiresAt().isAfter(now))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CmsContentResponse> getAllAdmin() {
        return cmsContentRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CmsContentResponse createContent(CreateCmsContentRequest request) {
        CmsContentEntity content = new CmsContentEntity();
        content.setContentType(request.contentType());
        content.setTitle(request.title());
        content.setBody(request.body());
        content.setPublished(request.isPublished());
        content.setPublishedAt(request.isPublished() ? OffsetDateTime.now() : null);
        content.setExpiresAt(request.expiresAt());
        return toResponse(cmsContentRepository.save(content));
    }

    @Transactional
    public CmsContentResponse updateContent(String id, UpdateCmsContentRequest request) {
        CmsContentEntity content = cmsContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CMS content not found"));
        content.setContentType(request.contentType());
        content.setTitle(request.title());
        content.setBody(request.body());
        content.setPublished(request.isPublished());
        if (request.isPublished() && content.getPublishedAt() == null) {
            content.setPublishedAt(OffsetDateTime.now());
        }
        if (!request.isPublished()) {
            content.setPublishedAt(null);
        }
        content.setExpiresAt(request.expiresAt());
        return toResponse(content);
    }

    @Transactional
    public void deleteContent(String id) {
        cmsContentRepository.deleteById(id);
    }

    private CmsContentResponse toResponse(CmsContentEntity entity) {
        return new CmsContentResponse(
                entity.getId(),
                entity.getContentType().name(),
                entity.getTitle(),
                entity.getBody(),
                entity.isPublished(),
                entity.getPublishedAt() == null ? null : entity.getPublishedAt().toString(),
                entity.getExpiresAt() == null ? null : entity.getExpiresAt().toString(),
                entity.getCreatedAt().toString()
        );
    }
}
