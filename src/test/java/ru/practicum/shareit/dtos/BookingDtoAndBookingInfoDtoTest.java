package ru.practicum.shareit.dtos;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoAndBookingInfoDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;
    @Autowired
    private JacksonTester<BookingInfoDto> jsonBookingInfoDto;

    @Test
    public void serialize() throws Exception {
        User user = new User(1, "a", "a@mail.ru");
        User booker = new User(2, "b", "b@mail.ru");
        Item item = new Item(1, "a", "b", true, user);
        BookingInfoDto bookingInfoDto = new BookingInfoDto(1, LocalDateTime.of(1, 1, 1, 1, 1, 1), LocalDateTime.of(2, 2, 2, 2, 2, 2), item, booker, Status.APPROVED);
        JsonContent<BookingInfoDto> result = jsonBookingInfoDto.write(bookingInfoDto);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).extractingJsonPathValue("$.start")
                .isEqualTo(bookingInfoDto.getStart().toString());
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).extractingJsonPathValue("$.end")
                .isEqualTo(bookingInfoDto.getEnd().toString());
        assertThat(result).hasJsonPathNumberValue("$.item.id");
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPathNumberValue("$.booker.id");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(2);
        assertThat(result).hasJsonPathValue("$.status");
        assertThat(result).extractingJsonPathValue("$.status")
                .isEqualTo(bookingInfoDto.getStatus().toString());

    }

    @Test
    public void deserialize() throws Exception {
        BookingDto bookingDto = new BookingDto(2, 0, LocalDateTime.of(2022, 10, 28, 22, 22, 22), LocalDateTime.of(2022, 10, 29, 22, 31, 34));
        Gson gson = new Gson();
        String content = "{\"id\": 2, \"start\": \"2022-10-28T22:22:22\", \"end\": \"2022-10-29T22:31:34\"}";
        assertThat(jsonBookingDto.parseObject(content))
                .isEqualTo(bookingDto);

    }
}
