resource "aws_security_group" "this" {
  name        = "rds-sg-v2"
  description = "Allow PostgreSQL from EC2 SG"
  vpc_id      = var.vpc_id

  tags = {
    Name    = "rds-sg-v2"
    Version = "v2"
  }

  lifecycle {
    prevent_destroy       = true
    create_before_destroy = true
  }
}
