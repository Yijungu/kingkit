resource "aws_iam_user_policy_attachment" "user_attach_state" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.terraform_state.arn
  depends_on = [aws_iam_policy_version.terraform_state_v]
}

resource "aws_iam_user_policy_attachment" "user_attach_ec2" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.ec2_control.arn
  depends_on = [aws_iam_policy_version.ec2_control_v]
}

resource "aws_iam_user_policy_attachment" "user_attach_iam_rds" {
  user       = "terraform-github-actions"
  policy_arn = aws_iam_policy.iam_rds.arn
  depends_on = [aws_iam_policy_version.iam_rds_v]
}
