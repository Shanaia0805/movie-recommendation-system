#!/bin/bash
set -e

# 1. Update and install basic dependencies
sudo apt-get update -y
sudo apt-get install -y openjdk-17-jdk python3-pip python3-venv nginx unzip

# 2. Install CloudWatch Agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb -O /tmp/amazon-cloudwatch-agent.deb
sudo dpkg -i /tmp/amazon-cloudwatch-agent.deb
sudo mkdir -p /opt/aws/amazon-cloudwatch-agent/etc
sudo cp /tmp/amazon-cloudwatch-agent.json /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

# 3. Create app directories
sudo mkdir -p /opt/cinevault/backend
sudo mkdir -p /opt/cinevault/recommendation
sudo mkdir -p /var/www/cinevault/frontend
sudo chown -R ubuntu:ubuntu /opt/cinevault /var/www/cinevault

# 4. Configure Nginx
# We will overwrite the default nginx config later with nginx.conf
sudo rm -f /etc/nginx/sites-enabled/default

# 5. Start CloudWatch Agent
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config -m ec2 -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json -s

echo "Base environment setup complete."
