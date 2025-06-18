locals {
  terraform_state_policy = file("${path.module}/../../policies/terraform-state-access.json")
  ec2_control_policy     = file("${path.module}/../../policies/ec2-control.json")
  iam_rds_policy         = file("${path.module}/../../policies/iam-ssm-rds.json")
}

### Terraform State용 정책
resource "aws_iam_policy" "terraform_state" {
  name        = "terraform-state-access"
  path        = "/"
  description = "Access to S3 and DynamoDB for Terraform backend"
}

resource "aws_iam_policy_version" "terraform_state_v" {
  policy_arn     = aws_iam_policy.terraform_state.arn
  policy         = local.terraform_state_policy
  set_as_default = true

  lifecycle {
    create_before_destroy = true
  }
}

### EC2 제어용 정책
resource "aws_iam_policy" "ec2_control" {
  name        = "ec2-control"
  path        = "/"
  description = "Control permissions for EC2 provisioning"
}

resource "aws_iam_policy_version" "ec2_control_v" {
  policy_arn     = aws_iam_policy.ec2_control.arn
  policy         = local.ec2_control_policy
  set_as_default = true

  lifecycle {
    create_before_destroy = true
  }
}

### IAM + SSM + RDS 정책
resource "aws_iam_policy" "iam_rds" {
  name        = "iam-ssm-rds"
  path        = "/"
  description = "IAM, SSM, and RDS management permissions"
}

resource "aws_iam_policy_version" "iam_rds_v" {
  policy_arn     = aws_iam_policy.iam_rds.arn
  policy         = local.iam_rds_policy
  set_as_default = true

  lifecycle {
    create_before_destroy = true
  }
}
