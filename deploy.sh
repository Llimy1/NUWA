#!/bin/bash

# Redis 컨테이너 상태 확인 및 시작
IS_REDIS_RUNNING=$(docker ps | grep redis)
if [ -z "$IS_REDIS_RUNNING" ]; then
    echo "Starting Redis container..."
    docker-compose up -d redis
else
    echo "Redis container is already running."
fi

# Monitoring 컨테이너 상태 확인
IS_MONITOR_RUNNING=$(docker ps | grep monitor)
if [ -z "$IS_MONITOR_RUNNING" ]; then
  echo "Starting Monitoring container..."
  docker-compose up -d monitor
else
  echo "Monitor container is already running."
fi

IS_GREEN=$(docker ps | grep green)
DEFAULT_CONF="/etc/nginx/nginx.conf"

app_health_check() {
    local service=$1
    local log_line="Started NuwaBackendApplication in"

    echo "Waiting for 30 seconds before health check..."
    sleep 30

    echo "Starting health check. Waiting up to 60 seconds for '$log_line' in $service logs..."
    if timeout 60 bash -c -- "while ! docker-compose logs $service | grep -q '$log_line'; do sleep 1; done"; then
        echo "$service has started successfully within 90 seconds."
        return 0
    else
        echo "Failed to detect '$log_line' in $service logs within 90 seconds. Stopping $service..."
        docker-compose stop $service
        return 1
    fi
}

# 현재 실행중인 App이 blue이면 green으로, 그렇지 않으면 blue로 전환
if [ -z "$IS_GREEN" ]; then
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
        echo "Health check failed for green. Keeping the current state."
        exit 1
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
        echo "Health check failed for blue. Keeping the current state."
        exit 1
    fi
fi
