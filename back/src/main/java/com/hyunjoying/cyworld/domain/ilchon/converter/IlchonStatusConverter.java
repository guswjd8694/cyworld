package com.hyunjoying.cyworld.domain.ilchon.converter;

import com.hyunjoying.cyworld.common.converter.AbstractEnumConverter;
import com.hyunjoying.cyworld.domain.ilchon.entity.Ilchon;
import jakarta.persistence.Converter;

@Converter
public class IlchonStatusConverter extends AbstractEnumConverter<Ilchon.IlchonStatus> {
    public IlchonStatusConverter() {
        super(Ilchon.IlchonStatus.class);
    }
}
