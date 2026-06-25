resource "aws_iam_service_linked_role" "opensearch" {
  aws_service_name = "opensearchservice.amazonaws.com"
}

resource "aws_security_group" "opensearch_sg" {
  name   = "linkedin-opensearch-sg"
  vpc_id = module.vpc.vpc_id

  ingress {
    description     = "OpenSearch HTTPS from EKS nodes"
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Project = "linkedin-clone"
  }
}

resource "aws_opensearch_domain" "linkedin_opensearch" {
  depends_on = [aws_iam_service_linked_role.opensearch]
  domain_name    = "linkedin-search"
  engine_version = "OpenSearch_2.17"

  cluster_config {
    instance_type  = "t3.small.search"
    instance_count = 1
  }

  ebs_options {
    ebs_enabled = true
    volume_size = 10
    volume_type = "gp3"
  }

  vpc_options {
    subnet_ids         = [module.vpc.private_subnets[0]]
    security_group_ids = [aws_security_group.opensearch_sg.id]
  }

  encrypt_at_rest {
    enabled = true
  }

  node_to_node_encryption {
    enabled = true
  }

  domain_endpoint_options {
    enforce_https = true
  }

  tags = {
    Project = "linkedin-clone"
  }
}