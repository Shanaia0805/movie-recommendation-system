source "amazon-ebs" "webapp" {
  ami_name      = "my-webapp-ami-{{timestamp}}"      # AMI name with timestamp
  instance_type = "t2.micro"                         # EC2 instance type for building the AMI
  region        = "us-east-2"                        # AWS region
  source_ami    = "ami-0cb91c7de36eed2cb"            # Ubuntu 24.04 AMI ID
  ssh_username  = "ubuntu"                           # Default SSH username for Ubuntu
  associate_public_ip_address = true                # Public IP for the instance
}

packer {
  required_plugins {
    amazon = {
      version = "~> 1.0"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

# Define the build steps to install dependencies and copy the app
build {
  sources = ["source.amazon-ebs.webapp"]

  provisioner "file" {
    source      = "movieServer-0.0.1-SNAPSHOT.jar"
    destination = "/home/ubuntu/app.jar"
  }

  provisioner "shell" {
    inline = [
      "sudo apt update",
      "sudo apt install -y openjdk-17-jre"
    ]
  }

}
