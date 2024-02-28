#!/bin/bash

# Redis 컨테이너 상태 확인 및 시작
IS_REDIS_RUNNING=$(docker ps | grep redis)
if [ -z "$IS_REDIS_RUNNING" ]; then
    echo "Starting Redis container..."
    docker-compose up -d redis
else
    echo "Redis container is already running."
fi

IS_GREEN=$(docker ps | grep green) # 현재 실행중인 App이 blue인지 확인
DEFAULT_CONF="/etc/nginx/nginx.conf"

rollback() {
    echo "Rollback to previous state..."
    if [ $1 == "green" ]; then
        sudo docker stop green
        sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
        sudo nginx -s reload
        sudo docker-compose up -d blue
    else
        sudo docker stop blue
        sudo cp /etc/nginx/nginx.green.conf /etc/nginx/nginx.conf
        sudo nginx -s reload
        sudo docker-compose up -d green
    fi
    echo "Rollback completed."
    exit 1
}

health_check() {
    local service=$1
    local retries=20 # 3초 간격으로 20번 시도, 총 1분
    local count=0

    while [ $count -lt $retries ]; do
        echo "$service health check attempt $(($count + 1))/$retries..."
        sleep 3
        REQUEST=$(curl -s http://127.0.0.1:$2) # $2는 서비스 포트

        if [ -n "$REQUEST" ]; then
            echo "Health check success."
            return 0
        fi
        ((count++))
    done

    echo "Health check failed."
    return 1
}

if [ -z $IS_GREEN ]; then # 현재 blue가 실행중이면 green으로 전환
    echo "### BLUE => GREEN ###"

    echo "1. Get green image"
    docker-compose pull green

    echo "2. Green container up"
    docker-compose up -d green

    if health_check "green" 8081; then
        echo "4. Reload nginx"
        sudo cp /etc/nginx/nginx.green.conf /etc/nginx/nginx.conf
        sudo nginx -s reload

        echo "5. Blue container down"
        sudo docker stop blue
    else
        rollback "green"
    fi
else
    echo "### GREEN => BLUE ###"

    echo "1. Get blue image"
    docker-compose pull blue

    echo "2. Blue container up"
    docker-compose up -d blue

    if health_check "blue" 8082; then
        echo "4. Reload nginx"
        sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
        sudo nginx -s reload

        echo "5. Green container down"
        sudo docker stop green
    else
        rollback "blue"
    fi
fi
