package com.practice.filmorate.mpa;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Mpa {
    private Long id;
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        return values;
    }
}
