package com.acare.backend.utils;

import jakarta.persistence.AttributeConverter;

public class AttributeEncryptor implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return AesUtil.encrypt(attribute, null);
    }

    @Override
    public String convertToEntityAttribute(String databaseColumn) {
        return AesUtil.decrypt(databaseColumn, null);
    }
}

