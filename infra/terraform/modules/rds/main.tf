resource "aws_db_instance" "this" {
  identifier              = var.identifier
  engine                  = "postgres"
  engine_version          = var.engine_version
  instance_class          = var.instance_class
  allocated_storage       = var.allocated_storage
  db_name                 = var.db_name
  username                = var.db_username
  password                = var.db_password
  publicly_accessible     = var.publicly_accessible
  skip_final_snapshot     = true
  deletion_protection     = false
  vpc_security_group_ids = var.security_group_ids
  backup_retention_period = var.backup_retention_period
}
