package ru.practicum.shareit.dtos;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoAndItemInfoDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;
    @Autowired
    private JacksonTester<ItemInfoDto> jsonItemInfoDto;

    @Test
    public void serialize() throws Exception {
        User user = new User(1, "a", "a@mail.ru");
        User booker = new User(2, "b", "b@mail.ru");
        Item item = new Item(1, "a", "b", true, user);
        Booking lastBooking = new Booking(1, LocalDateTime.of(1, 1, 1, 1, 1), LocalDateTime.of(2, 1, 1, 1, 1), item, booker, Status.APPROVED);
        Booking nextBooking = new Booking(2, LocalDateTime.of(4, 1, 1, 1, 1), LocalDateTime.of(5, 1, 1, 1, 1), item, booker, Status.APPROVED);

        ItemInfoDto itemInfoDto = ItemMapper.toItemInfoDto(item, lastBooking, nextBooking, new ArrayList<>());
        JsonContent<ItemInfoDto> result = jsonItemInfoDto.write(itemInfoDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(item.getName());
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(item.getDescription());
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(item.getAvailable());
        assertThat(result).hasJsonPathNumberValue("$.lastBooking.id");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPathNumberValue("$.nextBooking.id");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(2);
        assertThat(result).hasJsonPathArrayValue("$.comments");
        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .isEqualTo(new ArrayList<>());

    }

    @Test
    public void deserialize() throws Exception {
        ItemDto itemDto = new ItemDto(1, "a", "b", true, 1L);
        Gson gson = new Gson();
        String content = gson.toJson(itemDto);
        assertThat(jsonItemDto.parse(content))
                .isEqualTo(itemDto);
    }
}
