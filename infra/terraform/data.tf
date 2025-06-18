# ✅ Amazon Linux 2023 AMI (ARM64)
data "aws_ami" "amazon_linux_2023_arm64" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023*-arm64"]
  }

  filter {
    name   = "architecture"
    values = ["arm64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# ✅ 기본 VPC (서울 리전 기준, 기본 VPC 존재 가정)
data "aws_vpc" "default" {
  default = true
}

# ✅ 기본 서브넷 (서울 리전 AZ-a)
data "aws_subnet" "default" {
  default_for_az    = true
  availability_zone = "ap-northeast-2a"
}
