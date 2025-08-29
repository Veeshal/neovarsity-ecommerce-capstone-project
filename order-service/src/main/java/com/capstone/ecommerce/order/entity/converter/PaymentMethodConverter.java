package com.capstone.ecommerce.order.entity.converter;

import com.capstone.ecommerce.order.entity.PaymentMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethod attribute) {
        return attribute.getCode();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String code) {
        return PaymentMethod.from(code);
    }
}
