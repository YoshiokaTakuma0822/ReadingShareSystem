sudo dnf install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user
pip install fastapi uvicorn requests

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
sudo systemctl start a.service
