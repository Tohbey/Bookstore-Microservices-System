package com.bookstore.bookinventoryservice.entity.core;

import com.bookstore.bookinventoryservice.enums.Flag;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public class JpaConverter {

    public JpaConverter(){}

    @Converter
    public static class FlagConverter implements AttributeConverter<Flag, Integer>{
        public FlagConverter(){}

        @Override
        public Integer convertToDatabaseColumn(Flag flag) {

            return flag != null ? flag.getId() : Flag.NONE.getId();
        }

        @Override
        public Flag convertToEntityAttribute(Integer index) {

            return index != null ? Flag.getFlag(index) : Flag.NONE;
        }
    }
}
