import { authFetch } from "../../services/api";
import { apiBaseUrl } from "../../config/runtimeConfig";
import { Subscription } from "../../types/payment";

// Production: through the API gateway, like every other service.
//   gateway route /api/payments/** -> payment-service /**
// Local dev: set REACT_APP_PAYMENT_API_URL=http://localhost:8090 in .env.local
//   to talk to the payment service directly (no gateway/Redis needed).
const subscriptionsUrl = process.env.REACT_APP_PAYMENT_API_URL
  ? `${process.env.REACT_APP_PAYMENT_API_URL}/subscriptions`
  : `${apiBaseUrl}/api/payments/subscriptions`;

export async function createSubscription(
  userId: string,
  idempotencyKey: string
): Promise<Subscription> {
  const res = await authFetch(subscriptionsUrl, {
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
  const res = await authFetch(`${subscriptionsUrl}/${subscriptionId}/payment-intent`, {
    method: "POST",
  });
  if (!res.ok) throw new Error(`Failed to create payment intent: ${res.status}`);
  return (await res.json()).clientSecret;
}

export async function getSubscription(id: number): Promise<Subscription> {
  const res = await authFetch(`${subscriptionsUrl}/${id}`);
  if (!res.ok) throw new Error(`Failed to load subscription: ${res.status}`);
  return res.json();
}
