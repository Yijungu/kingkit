resource "aws_iam_role" "terraform_role" {
  name = "terraform-github-actions"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid = "AllowGithubActionsAssumeRole",
        Effect = "Allow",
        Principal = {
          AWS = "arn:aws:iam::010438505844:user/terraform-github-actions"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Project = "kingkit"
    Environment = "dev"
  }
}
