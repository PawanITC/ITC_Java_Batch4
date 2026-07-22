package com.itc.premiumsubscription.service;


import com.itc.premiumsubscription.dto.PlanRequestDTO;
import com.itc.premiumsubscription.dto.PlanUpdateDTO;
import com.itc.premiumsubscription.exception.ResourceNotFoundException;
import com.itc.premiumsubscription.model.Plan;
import com.itc.premiumsubscription.repository.PlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    @Transactional
    public Plan createPlan(PlanRequestDTO dto) {
        Plan plan = new Plan();
        plan.setPlanName(dto.getPlan_name());
        plan.setValidity(dto.getValidity());
        plan.setPrice(dto.getPrice());

        return planRepository.save(plan);
    }

    @Transactional
    public Plan updatePlan(Long id, PlanUpdateDTO dto) {
        Plan existingPlan = planRepository.findById(id)
                . orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + id));

        existingPlan.setPlanName(dto.getPlan_name());
        existingPlan.setValidity(dto.getValidity());
        existingPlan.setPrice(dto.getPrice());

        return planRepository.save(existingPlan);
    }

    @Transactional
    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    @Transactional
    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan not found with ID: " + id);
        }
        planRepository.deleteById(id);
    }
}
