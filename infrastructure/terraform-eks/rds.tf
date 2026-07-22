resource "aws_db_subnet_group" "linkedin_db_subnet_group" {
  name       = "linkedin-db-subnet-group"
  subnet_ids = module.vpc.private_subnets

  tags = {
    Project = "linkedin-clone"
  }
}

resource "aws_security_group" "rds_sg" {
  name   = "linkedin-rds-sg"
  vpc_id = module.vpc.vpc_id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "linkedin_postgres" {
  identifier             = "linkedin-postgres"
  engine                 = "postgres"
  engine_version         = "16"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  db_name                = "linkedin"
  username               = "postgres"
  password               = var.db_password
  port                   = 5432
  db_subnet_group_name   = aws_db_subnet_group.linkedin_db_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  skip_final_snapshot = true
  publicly_accessible = false
}