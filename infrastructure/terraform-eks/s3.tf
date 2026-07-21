resource "aws_s3_bucket" "app_uploads" {
  bucket = "${var.app_name}-${var.environment}-uploads-${var.aws_account_id}"

  tags = {
    Name        = "${var.app_name}-${var.environment}-uploads"
    Environment = var.environment
    Project     = var.app_name
  }
}

resource "aws_s3_bucket_public_access_block" "app_uploads" {
  bucket = aws_s3_bucket.app_uploads.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "app_uploads" {
  bucket = aws_s3_bucket.app_uploads.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_versioning" "app_uploads" {
  bucket = aws_s3_bucket.app_uploads.id

  versioning_configuration {
    status = "Suspended"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "app_uploads" {
  bucket = aws_s3_bucket.app_uploads.id

  rule {
    id     = "delete-temp-files-after-7-days"
    status = "Enabled"

    filter {
      prefix = "temp/"
    }

    expiration {
      days = 7
    }
  }

  rule {
    id     = "delete-exports-after-30-days"
    status = "Enabled"

    filter {
      prefix = "exports/"
    }

    expiration {
      days = 30
    }
  }
}

data "aws_iam_policy_document" "media_service_assume_role" {
  statement {
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [module.eks.oidc_provider_arn]
    }

    condition {
      test     = "StringEquals"
      variable = "${replace(module.eks.cluster_oidc_issuer_url, "https://", "")}:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringEquals"
      variable = "${replace(module.eks.cluster_oidc_issuer_url, "https://", "")}:sub"
      values   = ["system:serviceaccount:linkedin-prod:media-service-account"]
    }
  }
}

resource "aws_iam_role" "media_service" {
  name               = "${var.app_name}-${var.environment}-media-service"
  assume_role_policy = data.aws_iam_policy_document.media_service_assume_role.json

  tags = {
    Name        = "${var.app_name}-${var.environment}-media-service"
    Environment = var.environment
    Project     = var.app_name
  }
}

data "aws_iam_policy_document" "media_service_s3" {
  statement {
    sid = "MediaObjectAccess"
    actions = [
      "s3:AbortMultipartUpload",
      "s3:DeleteObject",
      "s3:GetObject",
      "s3:PutObject"
    ]

    resources = ["${aws_s3_bucket.app_uploads.arn}/posts/*"]
  }
}

resource "aws_iam_policy" "media_service_s3" {
  name        = "${var.app_name}-${var.environment}-media-service-s3"
  description = "Allow media services to upload and presign post media objects."
  policy      = data.aws_iam_policy_document.media_service_s3.json

  tags = {
    Name        = "${var.app_name}-${var.environment}-media-service-s3"
    Environment = var.environment
    Project     = var.app_name
  }
}

resource "aws_iam_role_policy_attachment" "media_service_s3" {
  role       = aws_iam_role.media_service.name
  policy_arn = aws_iam_policy.media_service_s3.arn
}
