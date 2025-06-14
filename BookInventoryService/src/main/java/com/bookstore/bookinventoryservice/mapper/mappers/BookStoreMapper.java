package com.bookstore.bookinventoryservice.mapper.mappers;

import com.bookstore.bookinventoryservice.entity.BookStore;
import com.bookstore.bookinventoryservice.mapper.dtos.BookStoreDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface BookStoreMapper {
    BookStoreMapper INSTANCE = Mappers.getMapper(BookStoreMapper.class);

    BookStoreDTO bookStoreToBookStoreDTO(BookStore bookStore);

    BookStore bookStoreDTOToBookStore(BookStoreDTO bookStoreDTO);
}
