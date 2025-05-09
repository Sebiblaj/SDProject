package org.example.openaichat.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileContentDTO {

    private String path;
    private String filename;
    private String extension;
    private String content;

}
