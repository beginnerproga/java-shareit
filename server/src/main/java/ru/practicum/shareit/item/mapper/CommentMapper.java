package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, User user, Item item, LocalDateTime created) {
        return new Comment(commentDto.getId(), commentDto.getText(), item, user, created);
    }

    public static CommentInfoDto toCommentInfoDto(Comment comment) {
        return new CommentInfoDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
