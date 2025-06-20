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
