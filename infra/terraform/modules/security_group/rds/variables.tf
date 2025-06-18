variable "name" {
  description = "보안 그룹 이름"
  type        = string
}

variable "description" {
  description = "보안 그룹 설명"
  type        = string
}

variable "ingress_from_port" {
  description = "인바운드 시작 포트"
  type        = number
}

variable "ingress_to_port" {
  description = "인바운드 종료 포트"
  type        = number
}

variable "ingress_protocol" {
  description = "인바운드 프로토콜 (예: tcp)"
  type        = string
}

variable "ingress_cidr_blocks" {
  description = "허용할 인바운드 CIDR 목록"
  type        = list(string)
}

variable "ec2_sg_id" {
  type        = string
  description = "EC2 인스턴스의 보안 그룹 ID"
}
