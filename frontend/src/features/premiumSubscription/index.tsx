import React, { useState } from 'react';
import { useSubscription } from '../../hooks/useSubscription';
import { SubscriptionButton } from './SubscriptionButton';
import { SubscriptionPlans } from './SubscriptionPlans';

export const SubscriptionFeature: React.FC = () => {
  const { plans, isLoading, error, fetchPlans, handleSelectPlan } = useSubscription();
  const [hasClicked, setHasClicked] = useState(false);

  const handleShowPlans = () => {
    setHasClicked(true);
    fetchPlans();
  };

  return (
    <div className="text-center py-10">
      {!hasClicked && (
        <SubscriptionButton onClick={handleShowPlans} isLoading={isLoading} />
      )}

      {isLoading && <p className="text-gray-600 mt-4 animate-pulse">Fetching the best plans for you...</p>}
      {error && <p className="text-red-600 mt-4">⚠️ {error}</p>}
      
      {hasClicked && !isLoading && !error && (
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Choose Your Premium Plan</h2>
          <p className="text-gray-500 text-sm mt-1">Unlock microservice-driven career networking power.</p>
          <SubscriptionPlans plans={plans} onSelectPlan={(id) => handleSelectPlan(String(id))} />
        </div>
      )}
    </div>
  );
};