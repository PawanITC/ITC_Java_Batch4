import { useEffect, useMemo, useState } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import keycloak from "../features/auth/keycloak";
import CheckoutForm from "../features/payment/components/CheckoutForm";
import { createSubscription, createPaymentIntent, getSubscription } from "../features/payment/paymentApi";
import { Subscription } from "../types/payment";

// load the Stripe SDK once, with your PUBLISHABLE key (the safe, public one)
const stripePromise = loadStripe(
  process.env.REACT_APP_STRIPE_PK ?? "pk_test_REPLACE_ME"
);

export default function PremiumPage() {
  // ≈ @State private var sub: Subscription? = nil   …etc
  const [sub, setSub] = useState<Subscription | null>(null);
  const [clientSecret, setClientSecret] = useState<string | null>(null);
  const [paid, setPaid] = useState(false);
  const [active, setActive] = useState(false);
  const [loading, setLoading] = useState(false);

  // userId comes from the KEYCLOAK TOKEN — never typed in, never trusted from a field
  const userId = useMemo(
    () => (keycloak.tokenParsed?.preferred_username as string) ?? "demo-user",
    []
  );

  // ≈ a Timer that polls after payment until the webhook flips us ACTIVE, then invalidates itself
  useEffect(() => {
    if (!sub || !paid || active) return;
    const t = setInterval(async () => {
      const fresh = await getSubscription(sub.id);
      if (fresh.status === "ACTIVE") {
        setActive(true);
        clearInterval(t);
      }
    }, 2000);
    return () => clearInterval(t);   // ≈ .onDisappear { timer.invalidate() }
  }, [sub, paid, active]);

  // ≈ the ViewModel action the button calls
  async function goPremium() {
    setLoading(true);
    const created = await createSubscription(userId, crypto.randomUUID());
    setSub(created);
    setClientSecret(await createPaymentIntent(created.id));
    setLoading(false);
  }

  // ≈ var body: some View — switches on state
  return (
    <div className="min-h-screen bg-[#f3f2ef]">
      <main className="mx-auto max-w-xl px-4 py-8">
        {active ? (
          <div className="rounded-xl bg-white p-8 text-center shadow">
            <h1 className="text-2xl font-bold">🎉 You&apos;re Premium!</h1>
            <p className="mt-2 text-gray-600">
              Your premium-career plan is active. Check your notifications 🔔
            </p>
          </div>
        ) : (
          <div className="rounded-xl bg-white p-8 shadow">
            <h1 className="text-2xl font-bold">LinkedIn Premium Career</h1>
            <p className="mt-2 text-gray-600">
              Unlock InMail, who viewed your profile, and courses.
            </p>

            {!clientSecret && (
              <button
                onClick={goPremium}
                disabled={loading}
                className="mt-6 rounded-full bg-[#0a66c2] px-6 py-3 font-semibold text-white disabled:opacity-50"
              >
                {loading ? "Preparing…" : "Go Premium — £29.99/mo"}
              </button>
            )}

            {clientSecret && !paid && (
              <Elements stripe={stripePromise} options={{ clientSecret }}>
                <CheckoutForm onSuccess={() => setPaid(true)} />
              </Elements>
            )}

            {paid && (
              <p className="mt-4 font-semibold text-[#0a66c2]">
                ✅ Paid! Activating your Premium…
              </p>
            )}
          </div>
        )}
      </main>
    </div>
  );
}