package ru.practicum.shareit.dtos;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoAndCommentInfoDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;
    @Autowired
    private JacksonTester<CommentInfoDto> jsonCommentInfoDto;

    @Test
    public void serialize() throws Exception {
        CommentInfoDto commentInfoDto = new CommentInfoDto(1L, "a", "b", LocalDateTime.of(1, 1, 1, 1, 1, 1));
        JsonContent<CommentInfoDto> result = jsonCommentInfoDto.write(commentInfoDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentInfoDto.getText());
        assertThat(result).hasJsonPathStringValue("$.authorName");
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentInfoDto.getAuthorName());
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).extractingJsonPathValue("$.created")
                .isEqualTo(commentInfoDto.getCreated().toString());

    }

    @Test
    public void deserialize() throws Exception {
        CommentDto commentDto = new CommentDto(1, "a");
        Gson gson = new Gson();
        String content = gson.toJson(commentDto);
        assertThat(jsonCommentDto.parse(content))
                .isEqualTo(commentDto);

    }
}
