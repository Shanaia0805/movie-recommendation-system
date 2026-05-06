packer {
  required_plugins {
    amazon = {
      version = ">= 1.2.8"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-04a81a99f5ec58529" # Ubuntu 24.04 in us-east-1
}

source "amazon-ebs" "cinevault" {
  ami_name      = "cinevault-fullstack-{{timestamp}}"
  instance_type = "t3.micro" # Use a micro instance as requested
  region        = var.aws_region
  subnet_id     = "subnet-02fca54d51d781b9c"
  source_ami    = var.source_ami
  ssh_username  = "ubuntu"
  associate_public_ip_address = true
}

build {
  sources = ["source.amazon-ebs.cinevault"]

  # 1. Copy config and service files to /tmp
  provisioner "file" {
    source      = "packer/amazon-cloudwatch-agent.json"
    destination = "/tmp/amazon-cloudwatch-agent.json"
  }

  provisioner "file" {
    source      = "packer/nginx.conf"
    destination = "/tmp/nginx.conf"
  }

  provisioner "file" {
    source      = "packer/cinevault-backend.service"
    destination = "/tmp/cinevault-backend.service"
  }

  provisioner "file" {
    source      = "packer/cinevault-recommender.service"
    destination = "/tmp/cinevault-recommender.service"
  }

  # 2. Copy application artifacts
  # Note: You must build these locally first!
  provisioner "file" {
    source      = "target/movieServer-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/app.jar"
  }

  provisioner "file" {
    source      = "recommendation"
    destination = "/tmp/"
  }

  provisioner "file" {
    source      = "frontend/dist"
    destination = "/tmp/"
  }

  # 3. Run setup script
  provisioner "shell" {
    script = "packer/web.sh"
  }

  # 4. Final configuration (Move files to final locations and enable services)
  provisioner "shell" {
    inline = [
      "sudo mv /tmp/app.jar /opt/cinevault/backend/app.jar",
      "sudo mv /tmp/recommendation/* /opt/cinevault/recommendation/",
      "sudo mv /tmp/dist/* /var/www/cinevault/frontend/",
      "sudo mv /tmp/nginx.conf /etc/nginx/sites-available/cinevault",
      "sudo ln -s /etc/nginx/sites-available/cinevault /etc/nginx/sites-enabled/cinevault",
      "sudo mv /tmp/cinevault-backend.service /etc/systemd/system/cinevault-backend.service",
      "sudo mv /tmp/cinevault-recommender.service /etc/systemd/system/cinevault-recommender.service",
      
      # Setup Python Virtual Environment
      "cd /opt/cinevault/recommendation && python3 -m venv /home/ubuntu/venv",
      "/home/ubuntu/venv/bin/pip install -r /opt/cinevault/recommendation/requirements.txt",
      
      # Enable services
      "sudo systemctl daemon-reload",
      "sudo systemctl enable nginx",
      "sudo systemctl enable cinevault-backend",
      "sudo systemctl enable cinevault-recommender"
    ]
  }
}
