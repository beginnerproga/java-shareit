package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfoDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemInfoDto.ItemBookingDto lastBooking;
    private ItemInfoDto.ItemBookingDto nextBooking;
    private List<CommentInfoDto> comments;

    public ItemInfoDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public static class ItemBookingDto {
        public Long id;
        public Long bookerId;

        public ItemBookingDto(Long id, Long bookerId) {
            this.id = id;
            this.bookerId = bookerId;
        }
    }

}
