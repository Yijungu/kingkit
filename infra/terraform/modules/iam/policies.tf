resource "aws_iam_policy" "terraform_state" {
  name        = "terraform-state-access"
  path        = "/"
  description = "Access to S3 and DynamoDB for Terraform backend"
  policy      = file("${path.module}/../../policies/terraform-state-access.json")
}

resource "aws_iam_policy" "ec2_control" {
  name        = "ec2-control"
  path        = "/"
  description = "Control permissions for EC2 provisioning"
  policy      = file("${path.module}/../../policies/ec2-control.json")
}

resource "aws_iam_policy" "iam_rds" {
  name        = "iam-ssm-rds"
  path        = "/"
  description = "IAM, SSM, and RDS management permissions"
  policy      = file("${path.module}/../../policies/iam-ssm-rds.json")
}

resource "aws_iam_policy" "ecr_push" {
  name        = "ecr-push"
  path        = "/"
  description = "ECR push permissions"
  policy      = file("${path.module}/../../policies/ecr-push.json")
}

resource "aws_iam_policy" "ssm_exec_policy" {
  name   = "ssm-send-command-policy"
  path        = "/"
  description = "SSM send permissions"
  policy = file("${path.module}/../../policies/ssm-exec-policy.json")
}