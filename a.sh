sudo dnf install -y docker

curl -sSL -J -O 'https://github.com/docker/compose/releases/download/v2.38.1/docker-compose-linux-aarch64'
sudo mkdir -p /usr/local/libexec/docker/cli-plugins/
sudo cp docker-compose-linux-aarch64 /usr/local/libexec/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/libexec/docker/cli-plugins/docker-compose

sudo service docker start
sudo usermod -a -G docker ec2-user
# Ensure pip for Python3 is installed
sudo dnf install -y python3-pip
# Install required Python packages via python3 -m pip
python3 -m pip install fastapi uvicorn requests

# systemdサービス定義を作成して出力
cat << 'EOF' | sudo tee /etc/systemd/system/a.service
[Unit]
Description=Run a.py at startup
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/python3 /home/ec2-user/a.py
Restart=always

[Install]
WantedBy=multi-user.target
EOF

# systemdをリロードしてサービスを有効化および起動
sudo systemctl daemon-reload
sudo systemctl enable a.service
sudo systemctl restart a.service

journalctl -u a.service -f
