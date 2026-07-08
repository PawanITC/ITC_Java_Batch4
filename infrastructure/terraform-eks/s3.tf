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