resource "aws_security_group" "this" {
  name        = "rds-security-group"
  description = "Allow PostgreSQL access"

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # 예전에는 security_groups가 아니었음
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "rds-security-group"
  }

  lifecycle {
    prevent_destroy = true  # 원래 있었던 경우엔 유지
  }
}
