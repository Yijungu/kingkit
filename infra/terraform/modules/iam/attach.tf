resource "aws_iam_role_policy_attachment" "attach_state" {
  role       = aws_iam_role.terraform_role.name
  policy_arn = aws_iam_policy.terraform_state.arn
}

resource "aws_iam_role_policy_attachment" "attach_ec2" {
  role       = aws_iam_role.terraform_role.name
  policy_arn = aws_iam_policy.ec2_control.arn
}

resource "aws_iam_role_policy_attachment" "attach_iam_rds" {
  role       = aws_iam_role.terraform_role.name
  policy_arn = aws_iam_policy.iam_rds.arn
}
