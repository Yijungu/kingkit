variable "name" {
  description = "EC2 인스턴스 이름"
  type        = string
}

variable "ami_id" {
  description = "EC2에 사용할 AMI ID"
  type        = string
}

variable "instance_type" {
  description = "EC2 인스턴스 타입"
  type        = string
}

variable "subnet_id" {
  description = "서브넷 ID"
  type        = string
}

variable "security_group_ids" {
  description = "List of security group IDs to associate with RDS"
  type        = list(string)
}

variable "iam_instance_profile" {
  description = "SSM용 IAM 인스턴스 프로필 이름"
  type        = string
}
