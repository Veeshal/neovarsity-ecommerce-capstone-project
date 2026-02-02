package com.capstone.ecommerce.notification.repository;

import com.capstone.ecommerce.notification.entity.EmailTemplate;
import com.capstone.ecommerce.notification.entity.EmailTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    @Query("""
        SELECT et FROM EmailTemplate et
        WHERE et.templateName = :name
        ORDER BY et.version DESC
    """)
    Optional<EmailTemplate> findLatest(@Param("name") EmailTemplateType templateName);
}