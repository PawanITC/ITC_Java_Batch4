export type FeedPost = {
  id: number;
  authorId: string;
  authorName: string;
  authorHeadline: string;
  content: string;
  likesCount: number;
  commentsCount: number;
  createdAt: string;
};