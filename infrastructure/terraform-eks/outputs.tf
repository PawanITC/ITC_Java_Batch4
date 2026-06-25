output "cluster_name" {
  value = module.eks.cluster_name
}

output "region" {
  value = var.aws_region
}

output "vpc_id" {
  value = module.vpc.vpc_id
}

output "rds_endpoint" {
  value = aws_db_instance.linkedin_postgres.address
}

output "msk_cluster_arn" {
  value = aws_msk_cluster.linkedin_msk.arn
}

output "msk_bootstrap_brokers" {
  value = aws_msk_cluster.linkedin_msk.bootstrap_brokers
}

output "opensearch_endpoint" {
  value = aws_opensearch_domain.linkedin_opensearch.endpoint
}