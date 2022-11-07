package ru.practicum.shareit.dtos;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoAndItemRequestInfoDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequestDto;
    @Autowired
    private JacksonTester<ItemRequestInfoDto> jsonItemInfoRequestDto;

    @Test
    public void serialize() throws Exception {
        List<ItemRequestInfoDto.ItemForRequest> itemForRequests = new ArrayList<>();
        itemForRequests.add(new ItemRequestInfoDto.ItemForRequest(1, "f", "b", true, 3));
        ItemRequestInfoDto itemRequestInfoDto = new ItemRequestInfoDto(1, "a", LocalDateTime.of(1, 1, 1, 1, 1, 1), itemForRequests);

        JsonContent<ItemRequestInfoDto> result = jsonItemInfoRequestDto.write(itemRequestInfoDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestInfoDto.getDescription());
        assertThat(result).hasJsonPathValue("$.description");
        assertThat(result).extractingJsonPathValue("$.created")
                .isEqualTo(itemRequestInfoDto.getCreated().toString());
        assertThat(result).hasJsonPathValue("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id")
                .isEqualTo(1);
    }

    @Test
    public void deserialize() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "a");
        Gson gson = new Gson();
        String content = gson.toJson(itemRequestDto);
        assertThat(jsonItemRequestDto.parse(content))
                .isEqualTo(itemRequestDto);

    }
}
