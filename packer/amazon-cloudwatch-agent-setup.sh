#!/bin/bash
set -e

# Install CloudWatch Agent
echo "Downloading CloudWatch Agent..."
wget -q https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i -E ./amazon-cloudwatch-agent.deb
rm -f amazon-cloudwatch-agent.deb

# Ensure config directories exist
sudo mkdir -p /opt/aws/amazon-cloudwatch-agent/etc

# Copy CloudWatch configuration files
echo "Copying CloudWatch Agent configuration files..."
sudo cp /tmp/amazon-cloudwatch-agent.json /opt/aws/amazon-cloudwatch-agent/etc/

# Apply CloudWatch Agent configuration
echo "Applying CloudWatch Agent configuration..."
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config \
  -m ec2 \
  -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json \
  -s

# Enable and restart CloudWatch Agent service
echo "Enabling and restarting CloudWatch Agent..."
sudo systemctl enable amazon-cloudwatch-agent
sudo systemctl restart amazon-cloudwatch-agent

echo "CloudWatch Agent setup completed successfully!"
