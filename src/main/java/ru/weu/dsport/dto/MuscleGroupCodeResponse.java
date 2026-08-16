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
@Schema(description = "Локализованная группа мышц")
public class MuscleGroupCodeResponse {

    @Schema(description = "Стабильный код группы мышц", example = "CHEST")
    private String code;

    @Schema(description = "Локализованное название группы мышц", example = "Chest")
    private String name;
}
