export interface Subscription {
  id: number;
  userId: string;
  plan: string;
  status: "PENDING" | "PROCESSING" | "ACTIVE" | "PAST_DUE" | "CANCELED";
  amount: number;
  currency: string;
}