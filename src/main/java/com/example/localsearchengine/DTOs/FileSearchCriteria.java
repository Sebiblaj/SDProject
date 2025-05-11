package com.example.localsearchengine.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FileSearchCriteria {

    private List<String> names;
    private List<String> paths;
    private List<String> keywords;
}
