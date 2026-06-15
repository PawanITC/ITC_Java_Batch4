import React from 'react';
import { Button } from '../../components/premiumSubscription/Button';

interface SubscriptionButtonProps {
  onClick: () => void;
  isLoading: boolean;
}

export const SubscriptionButton: React.FC<SubscriptionButtonProps> = ({ onClick, isLoading }) => {
  return (
    <Button onClick={onClick} disabled={isLoading}>
      {isLoading ? 'Loading Premium Details...' : 'Premium Subscription'}
    </Button>
  );
};