pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build API Gateway') {
            steps {
                dir('backend/api-gateway') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Build Search Service') {
            steps {
                dir('ITC_Java_Batch4/backend/searchAndDiscover') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Feed Service') {
            steps {
                dir('ITC_Java_Batch4/backend/feedAndTimeline') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        stage('Build Docker Images') {
            steps {
                sh 'docker build -t api-gateway:latest ITC_Java_Batch4/backend/api-gateway'
                sh 'docker build -t search-service:latest ITC_Java_Batch4/backend/searchAndDiscover'
                sh 'docker build -t feed-service:latest ITC_Java_Batch4/backend/feedAndTimeline'
            }
        }
    }
    stage('Docker Login') {
        steps {
            withCredentials([usernamePassword(
                credentialsId: 'dockerhub-credentials',
                usernameVariable: 'DOCKER_USER',
                passwordVariable: 'DOCKER_PASS'
            )]) {
                sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
            }
        }
    }

    stage('Tag Docker Images') {
        steps {
            sh 'docker tag api-gateway:latest shubhratripathi16/api-gateway:latest'
            sh 'docker tag search-service:latest shubhratripathi16/search-service:latest'
            sh 'docker tag feed-service:latest shubhratripathi16/feed-service:latest'
        }
    }

    stage('Push Docker Images') {
        steps {
            sh 'docker push shubhratripathi16/api-gateway:latest'
            sh 'docker push shubhratripathi16/search-service:latest'
            sh 'docker push shubhratripathi16/feed-service:latest'
        }
    }

    stage('Deploy with Docker Compose') {
        steps {
            dir('ITC_Java_Batch4') {
                sh 'docker compose down || true'
                sh 'docker compose up -d --build'
            }
        }
    }
}