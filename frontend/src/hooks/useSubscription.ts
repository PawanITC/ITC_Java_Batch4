import { useSubscriptionStore } from '../store/useSubscriptionStore';
import { subscriptionService } from '../services/subscriptionService';

export const useSubscription = () => {
  const { plans, isLoading, error, fetchPlans } = useSubscriptionStore();

  const handleSelectPlan = async (planId: string) => {
    try {
      const { checkoutUrl } = await subscriptionService.createCheckoutSession(planId);
      // Redirect user to Stripe Checkout hosted page
      window.location.href = checkoutUrl;
    } catch (err: any) {
  console.error('Checkout initialization failed:', err);
  
  // Extract the specific backend error message text if available
  const errorMessage = err.response?.data?.message || err.message || 'Could not initiate payment.';
  
  alert(`Payment Error: ${errorMessage}`);
}
  };

  return {
    plans,
    isLoading,
    error,
    fetchPlans,
    handleSelectPlan,
  };
};