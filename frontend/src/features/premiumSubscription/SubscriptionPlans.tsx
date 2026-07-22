import React from 'react';
import { SubscriptionPlan } from '../../types/subscription';
import { formatCurrency } from '../../utils/formatCurrency';
import { Button } from '../../components/premiumSubscription/Button';

interface SubscriptionPlansProps {
  plans: SubscriptionPlan[];
  // Changed id from string to bigint to match the interface
  onSelectPlan: (id: bigint) => void; 
}

export const SubscriptionPlans: React.FC<SubscriptionPlansProps> = ({ plans, onSelectPlan }) => {
  // 👇 Add this explicit safety check right here:
  if (!plans || !Array.isArray(plans)) {
    return (
      <div className="text-red-500 mt-4">
        ⚠️ Expected an array of plans, but received a different data format.
      </div>
    );
  }

  if (plans.length === 0) return null;

  return (
    <div className="mt-8 grid gap-6 md:grid-cols-2 max-w-4xl mx-auto">
      {plans.map((plan) => (
  // Ensure 'plan.id' matches whatever your primary key column name is (e.g. plan.planId or plan.id)
  <div key={plan.id} className="border border-gray-200 bg-white rounded-xl p-6 shadow-sm flex flex-col justify-between">
    <div>
      <h3 className="text-xl font-bold text-gray-900">{plan.planName}</h3>
      <p className="text-gray-500 text-sm mt-2">{plan.description}</p>
      
      <div className="my-4">
        {/* If your backend uses a different field name like plan.cost, change plan.price below */}
        <span className="text-3xl font-extrabold text-gray-900">${plan.price}</span>
        <span className="text-gray-500 text-sm"> / {plan.validity}</span>
      </div>
    </div>

    <button 
      className="w-full mt-4 bg-[#0a66c2] text-white py-2 rounded-full font-semibold hover:bg-[#004182]"
      onClick={() => onSelectPlan(plan.id)}
    >
      Upgrade Now
    </button>
  </div>
))}
    </div>
  );
};