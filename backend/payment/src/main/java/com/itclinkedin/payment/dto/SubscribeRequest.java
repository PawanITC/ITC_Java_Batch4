package com.itclinkedin.payment.dto;

import java.math.BigDecimal;

public record SubscribeRequest(

      String userId,
      String plan,
      BigDecimal amount,
      String currency ,
      String idempotencyKey


) {
}
