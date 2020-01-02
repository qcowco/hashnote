package com.project.hashnote.note.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StringBytesMapper {

    @Named("toBytes")
    default byte[] stringToBytes(String content){
        return content.getBytes();
    }

    @Named("toString")
    default String bytesToString(byte[] content){
        return new String(content);
    }

}
