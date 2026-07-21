import { FormEvent, useState } from "react";
import { PaymentElement, useStripe, useElements } from "@stripe/react-stripe-js";

interface Props {
  onSuccess: () => void;
}

export default function CheckoutForm({ onSuccess }: Props) {
  const stripe = useStripe();
  const elements = useElements();
  const [error, setError] = useState("");
  const [paying, setPaying] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!stripe || !elements) return;
    setPaying(true);

    // card data goes straight from Stripe's iframe to Stripe — never touches our code
    const result = await stripe.confirmPayment({
      elements,
      redirect: "if_required",
    });

    if (result.error) {
      setError(result.error.message ?? "Payment failed");
      setPaying(false);
    } else if (result.paymentIntent?.status === "succeeded") {
      onSuccess();
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mt-4">
      <PaymentElement />
      <button
        type="submit"
        disabled={!stripe || paying}
        className="mt-4 rounded-full bg-[#0a66c2] px-6 py-3 font-semibold text-white disabled:opacity-50"
      >
        {paying ? "Processing…" : "Pay £29.99"}
      </button>
      {error && <p className="mt-2 text-red-600">{error}</p>}
    </form>
  );
}