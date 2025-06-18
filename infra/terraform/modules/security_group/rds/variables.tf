variable "vpc_id" {
  description = "VPC ID for the RDS security group"
  type        = string
}

variable "ec2_sg_id" {
  description = "EC2 security group ID allowed to access RDS"
  type        = string
}
