variable "aws_region" {
  default = "eu-west-2"
}

variable "cluster_name" {
  default = "linkedin-eks"
}

variable "node_instance_type" {
  default = "t2.medium"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "app_name" {
  default = "linkedin-clone"
}

variable "environment" {
  default = "dev"
}

variable "aws_account_id" {
  default = "160198385987"
}