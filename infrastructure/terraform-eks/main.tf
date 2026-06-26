provider "aws" {
  region  = var.aws_region
  profile = "linkedin-clone"
}

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.21.0"

  name = "linkedin-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["eu-west-2a", "eu-west-2b"]
  public_subnets  = ["10.0.1.0/24", "10.0.2.0/24"]
  private_subnets = ["10.0.11.0/24", "10.0.12.0/24"]

  enable_nat_gateway = true
  single_nat_gateway = true

  tags = {
    Project = "linkedin-clone"
  }
}

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "20.37.1"

  cluster_name    = var.cluster_name
  cluster_version = "1.31"

  subnet_ids = module.vpc.private_subnets
  vpc_id     = module.vpc.vpc_id

  cluster_endpoint_public_access  = true
  cluster_endpoint_private_access = false

  enable_cluster_creator_admin_permissions = true

  eks_managed_node_groups = {
    default = {
      min_size       = 2
      max_size       = 4
      desired_size   = 3
      instance_types = [var.node_instance_type]
    }
  }

  tags = {
    Project = "linkedin-clone"
  }
}