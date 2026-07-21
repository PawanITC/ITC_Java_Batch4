import { authFetch } from "../../services/api";
import { Subscription } from "../../types/payment";

// direct to the payment service for local dev; via the gateway in prod
const paymentBaseUrl =
  process.env.REACT_APP_PAYMENT_API_URL ?? "http://localhost:8090";

// ≈ func createSubscription(userId: String, idempotencyKey: String) async throws -> Subscription
export async function createSubscription(
  userId: string,
  idempotencyKey: string
): Promise<Subscription> {
  const res = await authFetch(`${paymentBaseUrl}/subscriptions`, {
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
    `${paymentBaseUrl}/subscriptions/${subscriptionId}/payment-intent`,
    { method: "POST" }
  );
  if (!res.ok) throw new Error(`Failed to create payment intent: ${res.status}`);
  return (await res.json()).clientSecret;
}

export async function getSubscription(id: number): Promise<Subscription> {
  const res = await authFetch(`${paymentBaseUrl}/subscriptions/${id}`);
  if (!res.ok) throw new Error(`Failed to load subscription: ${res.status}`);
  return res.json();
}