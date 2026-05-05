packer {
  required_plugins {
    amazon = {
      version = ">= 1.0.0"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

# Define the source AMI (Ubuntu 24.04)
source "amazon-ebs" "ubuntu" {
  ami_name      = "mysql-{{timestamp}}"      # AMI name with timestamp
  instance_type = "t2.micro"                         # EC2 instance type for building the AMI
  region        = "us-west-2"                        # AWS region
  source_ami    = "ami-0448d5ebc7381d554"            # mysql ami
  ssh_username  = "ubuntu"                           # Default SSH username for Ubuntu
  vpc_id      = "vpc-0d899d2c3d9eb482f"   # Replace with your actual VPC ID
  subnet_id   = "subnet-0abbc6ea776965923" # Replace with your actual subnet ID
  associate_public_ip_address = false  


  launch_block_device_mappings {
    device_name = "/dev/sda1"
    volume_size = 8
    volume_type = "gp3"
    delete_on_termination = true
  }

  launch_block_device_mappings {
    device_name = "/dev/sdz"
    volume_size = 20
    volume_type = "gp3"
    delete_on_termination = false
    snapshot_id = "snap-05e53573cbeb7b373"
  }
}

# Define the build steps to install dependencies and copy the app
build {
  # Reference the source from above
  sources = ["source.amazon-ebs.ubuntu"]


  provisioner "file" {
    source      = "amazon-cloudwatch-agent.json"
    destination = "/tmp/amazon-cloudwatch-agent.json"
  }

  provisioner "shell" {
    script = "amazon-cloudwatch-agent-setup.sh"
  }
}
