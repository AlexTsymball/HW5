package com.example.hw5.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BookQueryDto {

    private String name;

    private String genre;

    private int offset;

    private int limit;

    public Map<String, Object> getAllParams(){
        Map<String, Object> params = new HashMap<>();
        if (name != null && !name.isBlank()) {
            params.put("name", name);
        }
        if (genre != null && !genre.isBlank()) {
            params.put("genre", genre);
        }
        if (offset != 0) {
            params.put("offset", offset);
        }
        if (limit != 0) {
            params.put("limit", limit);
        }
        return params;
    }
}