provider "aws" {
  region = var.region
}

module "iam" {
  source = "./modules/iam"
}

module "ssm_role" {
  source      = "./modules/iam/ssm_role"
  name        = "ec2-ssm-role"
  depends_on  = [module.iam]
}

module "ec2_sg" {
  source  = "./modules/security_group/ec2"
  vpc_id  = data.aws_vpc.default.id
  # ✅ 다른 파라미터는 필요 없음: 규칙은 내부에서 고정
}

module "rds_sg" {
  source              = "./modules/security_group/rds"
  name                = "rds-sg"
  description         = "Allow PostgreSQL access"
  ingress_from_port   = 5432
  ingress_to_port     = 5432
  ingress_protocol    = "tcp"
  ingress_cidr_blocks = ["0.0.0.0/0"]
  ec2_sg_id           = module.ec2_sg.security_group_id
  depends_on          = [module.iam]
}

module "auth_db" {
  source                  = "./modules/rds"
  identifier              = "auth-db"
  db_name                 = var.db_name
  db_username             = var.db_username
  db_password             = var.db_password

  # ✅ 두 SG를 동시에 붙임 (기존 + 신규)
  security_group_ids = [
    module.rds_sg.security_group_id,    # 기존 (rds_sg)
  ]
  depends_on        = [module.iam]
}

module "ec2" {
  source               = "./modules/ec2"
  name                 = "dev-ec2"
  instance_type        = "t4g.nano"
  ami_id               = data.aws_ami.amazon_linux_2023_arm64.id
  subnet_id            = data.aws_subnet.default.id
  security_group_id    = module.ec2_sg.security_group_id
  iam_instance_profile = module.ssm_role.instance_profile_name
  depends_on           = [module.iam]
}
