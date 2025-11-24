package ru.weu.dsport.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddSetRequest {
    @NotNull
    @Min(0)
    private Double weight;

    @NotNull
    @Min(1)
    private Integer repetitions;
}
