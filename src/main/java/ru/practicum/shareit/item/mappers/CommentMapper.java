package ru.practicum.shareit.item.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.models.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toDto(Comment comment);

    @Mapping(target = "authorName", source = "comment.author.name")
    List<CommentDto> toDtoList(List<Comment> commentList);
}
