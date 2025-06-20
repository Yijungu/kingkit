resource "aws_security_group" "this" {
  name        = "ec2-sg-v2"
  description = "Allow SSH access"
  vpc_id      = var.vpc_id

  tags = {
    Name = "ec2-sg"
  }

  lifecycle {
    prevent_destroy       = true
    create_before_destroy = true
  }
}
