variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-2" # Mumbai - cheapest region for India users
}

variable "app_name" {
  description = "Application name (used for naming all resources)"
  type        = string
  default     = "api-gateway"
}

variable "container_port" {
  description = "Port your Spring Boot app runs on"
  type        = number
  default     = 8085
}

variable "spring_profile" {
  description = "Spring active profile"
  type        = string
  default     = "dev"
}

# Smallest Fargate size that works for Spring Boot
variable "task_cpu" {
  description = "CPU units (256 = 0.25 vCPU)"
  type        = string
  default     = "512" # 0.5 vCPU
}

variable "task_memory" {
  description = "Memory in MB"
  type        = string
  default     = "1024" # 1 GB - minimum recommended for Spring Boot
}

variable "desired_count" {
  description = "Number of ECS tasks to run"
  type        = number
  default     = 1
}
