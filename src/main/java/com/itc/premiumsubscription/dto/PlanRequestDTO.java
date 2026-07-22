package com.itc.premiumsubscription.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PlanRequestDTO {

    @NotBlank(message = "Plan name is required")
    private String plan_name;

    @NotNull(message = "Validity is required")
    @Positive(message = "Validity must be greater than zero")
    private Integer validity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;
}
