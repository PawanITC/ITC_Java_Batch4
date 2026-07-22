import { authFetch } from "../../services/api";
import { apiBaseUrl } from "../../config/runtimeConfig";
import { Subscription } from "../../types/payment";

// All payment calls go through the API gateway (like every other service).
// Gateway route: /api/payments      -> payment-service /subscriptions
//                /api/payments/**   -> payment-service /**
const paymentsUrl = `${apiBaseUrl}/api/payments`;

export async function createSubscription(
  userId: string,
  idempotencyKey: string
): Promise<Subscription> {
  const res = await authFetch(paymentsUrl, {
    method: "POST",
    body: JSON.stringify({
      userId,
      plan: "premium-career",
      amount: 29.99,
      currency: "GBP",
      idempotencyKey,
    }),
  });
  if (!res.ok) throw new Error(`Failed to create subscription: ${res.status}`);
  return res.json();
}

export async function createPaymentIntent(subscriptionId: number): Promise<string> {
  const res = await authFetch(
    `${paymentsUrl}/subscriptions/${subscriptionId}/payment-intent`,
    { method: "POST" }
  );
  if (!res.ok) throw new Error(`Failed to create payment intent: ${res.status}`);
  return (await res.json()).clientSecret;
}

export async function getSubscription(id: number): Promise<Subscription> {
  const res = await authFetch(`${paymentsUrl}/subscriptions/${id}`);
  if (!res.ok) throw new Error(`Failed to load subscription: ${res.status}`);
  return res.json();
}
