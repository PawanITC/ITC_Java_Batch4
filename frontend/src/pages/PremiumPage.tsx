import React from 'react';
import { SubscriptionFeature } from '../features/premiumSubscription/index';

export const PremiumPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center px-4 sm:px-6 lg:px-8">
      <div className="max-w-md mx-auto text-center">
        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight sm:text-4xl">
          LinkedIn Premium
        </h1>
        <p className="mt-3 text-xl text-gray-500 sm:mt-4">
          Gain exclusive access to jobs, connections, and deeper analytics.
        </p>
      </div>
      
      <SubscriptionFeature />
    </div>
  );
};