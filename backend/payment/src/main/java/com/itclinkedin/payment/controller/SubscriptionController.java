package com.itclinkedin.payment.controller;

import com.itclinkedin.payment.dto.PaymentIntentResponse;
import com.itclinkedin.payment.dto.SubscribeRequest;
import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.service.PaymentSaga;
import com.itclinkedin.payment.service.StripePaymentService;
import com.itclinkedin.payment.service.SubscriptionService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")   // lets the browser frontend call this service
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;
    private final PaymentSaga paymentSaga;
    private final StripePaymentService stripePaymentService;

    public SubscriptionController(SubscriptionService service, PaymentSaga paymentSaga,
                                  StripePaymentService stripePaymentService) {
        this.service = service;
        this.paymentSaga = paymentSaga;
        this.stripePaymentService = stripePaymentService;
    }

    @PostMapping
    public ResponseEntity<Subscription> subscribe(@RequestBody SubscribeRequest request) {
        Subscription sub = service.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sub);
    }

    // the frontend's status poller: PENDING → ACTIVE after the webhook fires
    @GetMapping("/{id}")
    public ResponseEntity<Subscription> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // mock/test path — kept as a test seam behind the Strategy pattern
    @PostMapping("/{id}/pay")
    public ResponseEntity<Subscription> pay(@PathVariable Long id) {
        return ResponseEntity.ok(paymentSaga.pay(id));
    }

    // real Stripe: create a PaymentIntent → return its client_secret for Stripe.js
    @PostMapping("/{id}/payment-intent")
    public ResponseEntity<PaymentIntentResponse> paymentIntent(@PathVariable Long id) throws StripeException {
        String clientSecret = stripePaymentService.createPaymentIntent(id);
        return ResponseEntity.ok(new PaymentIntentResponse(clientSecret));
    }
}
