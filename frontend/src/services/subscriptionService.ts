import axios from 'axios';
import { SubscriptionPlan } from '../types/subscription';

// Premium subscription microservice route
const SUBSCRIPTION_API_URL = 'http://localhost:9000/api/v1/subscription'; 

export const subscriptionService = {
  getPlans: async (): Promise<SubscriptionPlan[]> => {
    const response = await axios.get<any>(SUBSCRIPTION_API_URL);
    
    console.log("Full Backend API Response Payload:", response.data);

    // 1. If it's already a clean raw array list
    if (Array.isArray(response.data)) {
      return response.data as SubscriptionPlan[];
    } 
    
    // 2. If it's wrapped inside a property named 'data'
    if (response.data && Array.isArray(response.data.data)) {
      return response.data.data as SubscriptionPlan[];
    }
    
    // 3. If it's wrapped inside a property named 'plans'
    if (response.data && Array.isArray(response.data.plans)) {
      return response.data.plans as SubscriptionPlan[];
    }
    
    // 4. If it's a Spring Pageable/Pagination response wrapped inside 'content'
    if (response.data && Array.isArray(response.data.content)) {
      return response.data.content as SubscriptionPlan[];
    }

    return [];
  },

  // 👇 ADD THIS METHOD BACK IN (This fixes the TS2339 Error) 👇
  createCheckoutSession: async (planId: string): Promise<{ checkoutUrl: string }> => {
    // Sends the selected plan id to your Spring Boot / Stripe microservice endpoint
    const response = await axios.post(`${SUBSCRIPTION_API_URL}/checkout`, { planId });
    return response.data;
  }
};