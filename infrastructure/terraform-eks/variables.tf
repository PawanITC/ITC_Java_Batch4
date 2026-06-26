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