package com.itc.premiumsubscription.controller;


import com.itc.premiumsubscription.dto.ApiResponseDTO;
import com.itc.premiumsubscription.dto.PlanRequestDTO;
import com.itc.premiumsubscription.dto.PlanUpdateDTO;
import com.itc.premiumsubscription.model.Plan;
import com.itc.premiumsubscription.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    // 1. Endpoint to create subscription (Only Admin)
    @PostMapping("/plan")
    public ResponseEntity<ApiResponseDTO<Plan>> createPlan(@Valid @RequestBody PlanRequestDTO planRequest) {
        Plan createdPlan = planService.createPlan(planRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Plan created successfully", createdPlan));
    }

    // 2. Endpoint to update the subscription (Only Admin)
    @PutMapping("/{plan_id}")
    public ResponseEntity<ApiResponseDTO<Plan>> updatePlan(
            @PathVariable("plan_id") Long planId,
            @Valid @RequestBody PlanUpdateDTO planUpdate) {

        Plan updatedPlan = planService.updatePlan(planId, planUpdate);
        return ResponseEntity.ok(ApiResponseDTO.success("Plan updated successfully", updatedPlan));
    }

    // 3. Endpoint to view all subscription plans (Accessible by any authenticated user)
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Plan>>> viewAllPlans() {
        List<Plan> plans = planService.getAllPlans();
        return ResponseEntity.ok(ApiResponseDTO.success("Subscription plans retrieved successfully", plans));
    }

    // 4. Endpoint to delete subscription (Only Admin)
    @DeleteMapping("/{plan_id}")
    public ResponseEntity<ApiResponseDTO<Void>> deletePlan(@PathVariable("plan_id") Long  planId) {
        planService.deletePlan(planId);
        return ResponseEntity.ok(ApiResponseDTO.success("Plan deleted successfully", null));
    }
}
