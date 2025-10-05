package com.hyunjoying.cyworld.common.converter;

import jakarta.persistence.AttributeConverter;

public abstract class AbstractEnumConverter<E extends Enum<E>> implements AttributeConverter<E, String> {

    private final Class<E> enumClass;

    public AbstractEnumConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        return Enum.valueOf(enumClass, dbData.toUpperCase());
    }
}
