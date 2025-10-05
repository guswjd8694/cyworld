package com.hyunjoying.cyworld.domain.user.converter;

import com.hyunjoying.cyworld.common.converter.AbstractEnumConverter;
import com.hyunjoying.cyworld.domain.user.entity.User;
import jakarta.persistence.Converter;

@Converter
public class GenderConverter extends AbstractEnumConverter<User.Gender> {
    public GenderConverter() {
        super(User.Gender.class);
    }
}
