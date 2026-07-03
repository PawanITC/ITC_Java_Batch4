export interface AppNotification {
  id: number;
  recipientUserId: string;
  type: string;
  content: string;
  eventId: string;
  read: boolean;
  createdAt: string;
}