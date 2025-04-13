provider "aws" {
  region = var.region
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnet_ids" "default" {
  vpc_id = data.aws_vpc.default.id
}

resource "aws_security_group" "rds_sg" {
  name        = "rds-security-group"
  description = "Allow PostgreSQL access"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # 가장 저렴한 테스트용이니 열어둠
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "rds-subnet-group"
  subnet_ids = slice(data.aws_subnet_ids.default.ids, 0, 2) # 가장 앞 2개만 사용

  tags = {
    Name = "RDS subnet group"
  }
}

resource "aws_db_instance" "auth_db" {
  identifier              = "auth-db"
  engine                  = "postgres"
  engine_version          = "14.11"                       
  instance_class          = "db.t4.micro"             # 가장 저렴한 요금제
  allocated_storage       = 20                        # 최소 스토리지 (GiB)
  db_name                 = var.db_name
  username                = var.db_username
  password                = var.db_password
  publicly_accessible     = true
  skip_final_snapshot     = true
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  db_subnet_group_name    = aws_db_subnet_group.rds_subnet_group.name
}
