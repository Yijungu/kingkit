resource "aws_instance" "this" {
  ami                         = var.ami_id
  instance_type               = var.instance_type

  user_data = file("${path.module}/scripts/user_data.sh")

  subnet_id                   = var.subnet_id
  vpc_security_group_ids      = [var.security_group_id]
  associate_public_ip_address = true

  iam_instance_profile        = var.iam_instance_profile  # ✅ 추가

  root_block_device {
    volume_size = 8
    volume_type = "gp3"
  }

  tags = {
    Name = var.name
  }
}
