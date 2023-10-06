#!/bin/bash -ex
exec > >(tee /var/log/user-data.log|logger -t user-data -s 2>/dev/console) 2>&1
# put your script here
sudo amazon-linux-extras install nginx1 -y
sudo openssl req -x509 -newkey rsa:4096 -keyout /etc/nginx/cert.key -out /etc/nginx/cert.crt -sha256 -days 3650 -nodes -subj "/C=AU/ST=NSW/L=Sydney/O=reInvent/OU=MSF/CN=Demo"

cat << EOF > /etc/nginx/conf.d/default.conf
server {
    listen 443;
    server_name \$host;
    rewrite ^/\$ https://\$host/ redirect;
    resolver 169.254.169.253 ipv6=off valid=5s;
    set \$dashboards_host $OPEN_SEARCH_ENDPOINT;

    ssl_certificate           /etc/nginx/cert.crt;
    ssl_certificate_key       /etc/nginx/cert.key;

    ssl on;
    ssl_session_cache  builtin:1000  shared:SSL:10m;
    ssl_protocols  TLSv1 TLSv1.1 TLSv1.2;
    ssl_ciphers HIGH:!aNULL:!eNULL:!EXPORT:!CAMELLIA:!DES:!MD5:!PSK:!RC4;
    ssl_prefer_server_ciphers on;

    location / {
        # Forward requests to Dashboards
        proxy_pass https://\$dashboards_host;
        proxy_set_header HOST \$host;

        # Update cookie domain and path
        proxy_cookie_domain \$dashboards_host \$host;

        # Response buffer settings
        proxy_buffer_size 128k;
        proxy_buffers 4 256k;
        proxy_busy_buffers_size 256k;
    }
}
EOF

sudo systemctl restart nginx.service

# cfn-signal
yum install python2-pip -y
python2 -m pip install https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-latest.tar.gz
cfn-signal -e $? --stack $STACK_NAME --resource NginxInstance --region $AWS_REGION 
