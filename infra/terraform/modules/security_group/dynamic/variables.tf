variable "security_groups" {
  description = "보안 그룹 정의 map - 이름을 키로 하고, ingress 규칙과 설명을 포함"
  type = map(object({
    description   = string
    ingress_rules = list(object({
      from_port   = number
      to_port     = number
      protocol    = string
      cidr_blocks = list(string)
    }))
  }))
}
