package com.example.projectstudy.db.diet_inform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Diet_Inform_Comment_Dto {
    @NotBlank
    private String comment;
}
