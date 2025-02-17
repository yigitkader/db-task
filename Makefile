# Project variables
GRADLE := ./gradlew
APP_NAME := deutche-bank-work
VERSION := 0.0.1-SNAPSHOT
JAR_PATH := build/libs/$(APP_NAME)-$(VERSION).jar


ENV ?= dev
JAVA_OPTS ?= -Xmx512m -Xms256m
SERVER_PORT ?= 8080

GREEN := \033[0;32m
YELLOW := \033[0;33m
NC := \033[0m # No Color

.DEFAULT_GOAL := help

#######################
# Local Development
#######################

# Build production JAR
build:
	@echo "$(GREEN)Building production JAR...$(NC)"
	@$(GRADLE) clean build -x test --no-daemon

# Run unit and integration tests
test:
	@echo "$(GREEN)Running all tests...$(NC)"
	@$(GRADLE) test --no-daemon

# Run application locally in production mode
local-run: build
	@echo "$(GREEN)Starting application in production mode...$(NC)"
	@java $(JAVA_OPTS) \
		-Dspring.profiles.active=$(ENV) \
		-Dserver.port=$(SERVER_PORT) \
		-jar $(JAR_PATH)

# Run application locally in development mode
local-dev:
	@echo "$(YELLOW)Starting application in development mode...$(NC)"
	@$(GRADLE) bootRun \
		-Dspring.profiles.active=dev \
		--no-daemon \
		--console=plain

# Kill process on local port
local-stop:
	@echo "$(YELLOW)Checking port $(SERVER_PORT)...$(NC)"
	@if lsof -ti :$(SERVER_PORT) > /dev/null; then \
		echo "$(YELLOW)Killing process on port $(SERVER_PORT)...$(NC)"; \
		lsof -ti :$(SERVER_PORT) | xargs kill -9; \
		echo "$(GREEN)Port $(SERVER_PORT) has been cleared$(NC)"; \
	else \
		echo "$(GREEN)No process found on port $(SERVER_PORT)$(NC)"; \
	fi

#######################
# Docker Operations
#######################

# Build Docker image
docker-build: build
	@echo "$(GREEN)Building Docker image...$(NC)"
	docker build \
		--build-arg JAR_FILE=$(JAR_PATH) \
		-t $(APP_NAME):$(VERSION) \
		-t $(APP_NAME):latest .

# Run with Docker
docker-run:
	@echo "$(GREEN)Running Docker container...$(NC)"
	@if [ -z "$$(docker images -q $(APP_NAME):latest)" ]; then \
		echo "$(YELLOW)Docker image not found. Building first...$(NC)"; \
		make docker-build; \
	fi
	docker run -d \
		-p $(SERVER_PORT):$(SERVER_PORT) \
		-e "SPRING_PROFILES_ACTIVE=$(ENV)" \
		-e "JAVA_OPTS=$(JAVA_OPTS)" \
		--name $(APP_NAME) \
		$(APP_NAME):latest

# Stop Docker container
docker-stop:
	@echo "$(YELLOW)Checking for Docker container...$(NC)"
	@if docker ps -q -f name=$(APP_NAME) > /dev/null; then \
		echo "$(YELLOW)Stopping Docker container...$(NC)"; \
		docker stop $(APP_NAME); \
		docker rm $(APP_NAME); \
		echo "$(GREEN)Docker container stopped and removed$(NC)"; \
	else \
		echo "$(GREEN)No Docker container found$(NC)"; \
	fi

#######################
# Utility Commands
#######################

# Clean build artifacts
clean:
	@echo "$(YELLOW)Cleaning build artifacts...$(NC)"
	@$(GRADLE) clean --no-daemon
	@rm -rf build/

# Show help
help:
	@echo "$(GREEN)Available commands:$(NC)"
	@echo ""
	@echo "$(YELLOW)Local Development:$(NC)"
	@echo "  make build         - Build production JAR"
	@echo "  make test          - Run all tests"
	@echo "  make local-run     - Run locally in production mode"
	@echo "  make local-dev     - Run locally in development mode"
	@echo "  make local-stop    - Stop local application"
	@echo ""
	@echo "$(YELLOW)Docker Operations:$(NC)"
	@echo "  make docker-build  - Build Docker image"
	@echo "  make docker-run    - Run with Docker"
	@echo "  make docker-stop   - Stop Docker container"
	@echo ""
	@echo "$(YELLOW)Utility:$(NC)"
	@echo "  make clean         - Clean build artifacts"
	@echo ""
	@echo "$(YELLOW)Environment variables:$(NC)"
	@echo "  ENV            - Environment profile (default: dev)"
	@echo "  JAVA_OPTS     - JVM options (default: -Xmx512m -Xms256m)"
	@echo "  SERVER_PORT   - Server port (default: 8080)"

.PHONY: build test local-run local-dev local-stop docker-build docker-run docker-stop clean help