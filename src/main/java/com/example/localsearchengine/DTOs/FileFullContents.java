package com.example.localsearchengine.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileFullContents {

    private FileDTO file;
    private ContentsDTO contents;
}
