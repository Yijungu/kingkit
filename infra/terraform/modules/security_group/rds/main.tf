resource "aws_security_group" "this" {
  name        = "rds-sg"
  description = "Allow PostgreSQL from EC2 SG"
  vpc_id      = var.vpc_id

  tags = {
    Name    = "rds-sg"
    Purpose = "Allow Postgres from EC2"
    Version = "v2"
  }

  lifecycle {
    prevent_destroy       = true
    create_before_destroy = true
  }
}
