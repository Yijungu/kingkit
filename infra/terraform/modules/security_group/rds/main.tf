resource "aws_security_group" "this" {
  name        = "rds-sg"
  description = "Allow PostgreSQL from EC2 SG"

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.ec2_sg_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "rds-sg"
    Purpose = "Allow Postgres from EC2"
    Version = "v2" 
  }
}

output "security_group_id" {
  value = aws_security_group.this.id
}
