provider "aws" {
  region = var.region
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
  source                 = "./modules/rds"
  identifier             = "auth-db"
  db_name                = var.db_name
  db_username            = var.db_username
  db_password            = var.db_password
  security_group_id      = module.rds_sg.security_group_id
}
