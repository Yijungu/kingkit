variable "identifier" {
  type = string
}

variable "engine_version" {
  type    = string
  default = "17.2"
}

variable "instance_class" {
  type    = string
  default = "db.t4g.micro"
}

variable "allocated_storage" {
  type    = number
  default = 20
}

variable "db_name" {
  type = string
}

variable "db_username" {
  type = string
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "publicly_accessible" {
  type    = bool
  default = true
}

variable "security_group_id" {
  type = string
}

variable "backup_retention_period" {
  type    = number
  default = 7
}
