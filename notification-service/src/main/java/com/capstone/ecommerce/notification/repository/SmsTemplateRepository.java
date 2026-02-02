package com.capstone.ecommerce.notification.repository;

import com.capstone.ecommerce.notification.entity.EmailTemplateType;
import com.capstone.ecommerce.notification.entity.SmsTemplate;
import com.capstone.ecommerce.notification.entity.SmsTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SmsTemplateRepository extends JpaRepository<SmsTemplate, Long> {
    @Query("""
            SELECT st FROM SmsTemplate st
            WHERE st.templateName = :name
            ORDER BY st.version DESC
    """)
    Optional<SmsTemplate> findLatest(@Param("name") SmsTemplateType templateName);
}