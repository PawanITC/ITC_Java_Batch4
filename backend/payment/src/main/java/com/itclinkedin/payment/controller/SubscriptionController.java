package com.itclinkedin.payment.controller;

import com.itclinkedin.payment.dto.SubscribeRequest;
import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.service.PaymentSaga;
import com.itclinkedin.payment.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;
    private final PaymentSaga paymentSaga;

    public SubscriptionController(SubscriptionService service, PaymentSaga paymentSaga) {
        this.service = service;
        this.paymentSaga = paymentSaga;
    }

    @PostMapping
    public ResponseEntity<Subscription> subscribe(@RequestBody SubscribeRequest request) {
        Subscription sub = service.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sub);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Subscription> pay(@PathVariable Long id) {
        return ResponseEntity.ok(paymentSaga.pay(id));
    }
}