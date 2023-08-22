package com.example.projectstudy.db.diet_inform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Diet_Inform_Post_Dto {

    @NotBlank
    private String tag; // 태그
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
