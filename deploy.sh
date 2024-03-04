#!/bin/bash

# Redis 컨테이너 상태 확인 및 시작
IS_REDIS_RUNNING=$(docker ps | grep redis)
if [ -z "$IS_REDIS_RUNNING" ]; then
    echo "Starting Redis container..."
    docker-compose up -d redis
else
    echo "Redis container is already running."
fi

# Mongo 컨테이너 상태 확인 및 시작
IS_REDIS_RUNNING=$(docker ps | grep mongo)
if [ -z "$IS_REDIS_RUNNING" ]; then
    echo "Starting Mongo container..."
    docker-compose up -d mongo
else
    echo "Mongo container is already running."
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

app_health_check() {
    local service=$1
    local log_line="Started NuwaBackendApplication in"

    echo "Waiting for 30 seconds before health check..."
    sleep 30

    echo "Checking if $service is up by searching for '$log_line' in logs..."
    if docker-compose logs $service | tail -n 100 | grep "$log_line"; then
        echo "$service has started successfully."
        return 0
    else
        echo "$service failed to start."
        return 1
    fi
}

if [ -z "$IS_GREEN" ]; then # 현재 blue가 실행중이면 green으로 전환
    echo "### BLUE => GREEN ###"

    echo "1. Get green image"
    docker-compose pull green

    echo "2. Green container up"
    docker-compose up -d green

    if app_health_check "green"; then
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

    if app_health_check "blue"; then
        echo "4. Reload nginx"
        sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
        sudo nginx -s reload

        echo "5. Green container down"
        sudo docker stop green
    else
        rollback "blue"
    fi
fi
