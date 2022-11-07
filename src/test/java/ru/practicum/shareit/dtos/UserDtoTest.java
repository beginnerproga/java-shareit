package ru.practicum.shareit.dtos;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<User> jsonUser;
    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Test
    public void testSerialize() throws Exception {
        User user = new User(1, "a", "b@mail.ru");
        JsonContent<User> result = jsonUser.write(user);
        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(user.getName());
        assertThat(result).hasJsonPathStringValue("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo(user.getEmail());

    }

    @Test
    public void testDeserialize() throws Exception {
        UserDto user = new UserDto(1, "a", "b@mail.ru");
        Gson gson = new Gson();
        String content = gson.toJson(user);
        assertThat(jsonUserDto.parse(content))
                .isEqualTo(user);
    }

}
