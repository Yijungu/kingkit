provider "aws" {
  region = var.region
}

resource "aws_security_group" "rds_sg" {
  name        = "rds-security-group"
  description = "Allow PostgreSQL access"

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_instance" "auth_db" {  
  identifier              = "auth-db"   
  engine                  = "postgres"
  engine_version          = "17.2"
  instance_class          = "db.t4g.micro"
  allocated_storage       = 20
  db_name                 = "authdb"    
  username                = var.db_username
  password                = var.db_password
  publicly_accessible     = true
  skip_final_snapshot     = true
  deletion_protection     = false
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  backup_retention_period = 7
}