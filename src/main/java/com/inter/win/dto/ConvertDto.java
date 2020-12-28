package com.inter.win.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConvertDto {
    private List<List<String>> sources;
    private List<String> target;
    private String frontId;
}
