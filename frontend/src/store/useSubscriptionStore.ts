import { create } from 'zustand';
import { SubscriptionPlan } from '../types/subscription';
import { subscriptionService } from '../services/subscriptionService';

interface SubscriptionState {
  plans: SubscriptionPlan[];
  isLoading: boolean;
  error: string | null;
  fetchPlans: () => Promise<void>;
}

export const useSubscriptionStore = create<SubscriptionState>((set) => ({
  plans: [],
  isLoading: false,
  error: null,
  fetchPlans: async () => {
    set({ isLoading: true, error: null });
    try {
      const plans = await subscriptionService.getPlans();
      set({ plans, isLoading: false });
    } catch (err: any) {
  // 1. Check if it's an Axios error containing a backend error body
  if (err.response && err.response.data) {
    // If your Spring Boot error payload has a 'message' field
    const backendMessage = err.response.data.message || JSON.stringify(err.response.data);
    set({ error: backendMessage, isLoading: false });
  } 
  // 2. Check if it's a generic network connectivity issue
  else if (err.message) {
    set({ error: err.message, isLoading: false });
  } 
  // 3. Absolute fallback
  else {
    set({ error: 'An unexpected connection error occurred', isLoading: false });
  }
}
  },
}));