package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ItemRequestInfoDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequest> items;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class ItemForRequest {
        private long id;
        private String name;
        private String description;
        private boolean available;
        private long requestId;

    }

}
