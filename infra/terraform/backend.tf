terraform {
  backend "s3" {
    bucket         = "terraform-state-king"
    key            = "kingkit/prod/terraform.tfstate"
    region         = "ap-northeast-2"
    encrypt        = true
    dynamodb_table = "terraform-locks-king"
  }
}
