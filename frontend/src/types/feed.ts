export type FeedPost = {
  id: number;
  postId?: number;
  authorId: string;
  authorName: string;
  authorHeadline: string;
  authorAvatarUrl?: string;
  content: string;
  mediaUrl?: string;
  mediaObjectKey?: string;
  mediaType?: "IMAGE" | "VIDEO";
  likesCount: number;
  commentsCount: number;
  createdAt: string;
};

export type CreatePostResponse = FeedPost;

export type FeedComment = {
  id: number;
  postId: number;
  authorId: string;
  authorName: string;
  content: string;
  createdAt: string;
};

export type MediaUploadResponse = {
  mediaUrl: string;
  mediaObjectKey?: string;
  mediaType: "IMAGE" | "VIDEO";
  objectKey: string;
};
