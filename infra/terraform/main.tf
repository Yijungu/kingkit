provider "aws" {
  region = var.region
}

module "ec2_sg" {
  source              = "./modules/security_group"
  name                = "ec2-sg"
  description         = "Allow SSH access"
  ingress_from_port   = 22
  ingress_to_port     = 22
  ingress_protocol    = "tcp"
  ingress_cidr_blocks = ["0.0.0.0/0"]
}

module "rds_sg" {
  source              = "./modules/security_group"
  name                = "rds-security-group"
  description         = "Allow PostgreSQL access"
  ingress_from_port   = 5432
  ingress_to_port     = 5432
  ingress_protocol    = "tcp"
  ingress_cidr_blocks = ["0.0.0.0/0"]
}

module "auth_db" {
  source            = "./modules/rds"
  identifier        = "auth-db"
  db_name           = var.db_name
  db_username       = var.db_username
  db_password       = var.db_password
  security_group_id = module.rds_sg.security_group_id
}

module "ssm_role" {
  source = "./modules/iam"
  name   = "ec2-ssm-role"
}

module "ec2" {
  source                = "./modules/ec2"
  name                  = "dev-ec2"
  instance_type         = "t4g.nano"
  ami_id                = data.aws_ami.amazon_linux_2023_arm64.id
  subnet_id             = data.aws_subnet.default.id
  security_group_id     = module.ec2_sg.security_group_id
  iam_instance_profile  = module.ssm_role.instance_profile_name  # ✅ 연결
}