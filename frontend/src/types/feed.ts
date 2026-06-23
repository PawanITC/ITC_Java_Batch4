export type FeedPost = {
  id: number;
  postId?: number;
  authorId: string;
  authorName: string;
  authorHeadline: string;
  content: string;
  likesCount: number;
  commentsCount: number;
  createdAt: string;
};

export type CreatePostResponse = FeedPost;
