#!/bin/bash
# ============================================
# Local Development Startup Script
# Movie Management System - Spring Boot Backend
# ============================================

export DB_URL="jdbc:mysql://127.0.0.1:3306/recommend?useSSL=false&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD=""
export JWT_SECRET="movie-management-system-local-dev-secret-key-32chars-min"

echo "🚀 Starting Spring Boot backend..."
echo "   DB: recommend @ localhost:3306"
echo "   Port: 8080"
echo ""

mvn spring-boot:run
