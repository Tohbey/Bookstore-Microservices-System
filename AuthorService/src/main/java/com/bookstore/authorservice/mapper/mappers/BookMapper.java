package com.bookstore.authorservice.mapper.mappers;

import com.bookstore.authorservice.entity.Book;
import com.bookstore.authorservice.mapper.dtos.BookDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDTO bookToBookDTO(Book book);

    Book bookDTOToBook(BookDTO bookDTO);
}
