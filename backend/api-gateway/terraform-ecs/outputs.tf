output "alb_dns_name" {
  description = "Load balancer URL to access your app"
  value       = "http://${aws_lb.main.dns_name}"
}

output "ecr_repository_url" {
  description = "ECR URL to push your Docker image"
  value       = aws_ecr_repository.app.repository_url
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.main.name
}

output "docker_push_commands" {
  description = "Commands to push your Docker image to ECR"
  value       = <<-EOT
    # 1. Authenticate Docker to ECR
    aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${aws_ecr_repository.app.repository_url}

    # 2. Build your Spring Boot image
    docker build -t ${var.app_name} .

    # 3. Tag the image
    docker tag ${var.app_name}:latest ${aws_ecr_repository.app.repository_url}:latest

    # 4. Push to ECR
    docker push ${aws_ecr_repository.app.repository_url}:latest
  EOT
}
