package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.utils.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Create.class})
    private String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
