package ru.weu.dsport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Код группы мышц")
public class MuscleGroupCodeResponse {

    @Schema(description = "Код группы мышц", example = "CHEST")
    private String code;
}
