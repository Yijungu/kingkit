resource "aws_iam_user_policy_attachment" "user_attach_state" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.terraform_state.arn
}

resource "aws_iam_user_policy_attachment" "user_attach_ec2" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.ec2_control.arn
}

resource "aws_iam_user_policy_attachment" "user_attach_iam_rds" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.iam_rds.arn
}

resource "aws_iam_user_policy_attachment" "user_attach_ecr_push" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.ecr_push.arn
}

resource "aws_iam_user_policy_attachment" "user_attach_ssm_exec_policy" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.ssm_exec_policy.arn
}
