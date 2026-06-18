pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = "your-dockerhub-username"
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
                dir('backend/searchAndDiscover') {
                    sh 'mvn clean verify'
                }
            }
        }

        stage('Build Feed Service') {
            steps {
                dir('backend/feedAndTimeline') {
                    sh 'mvn clean verify'
                }
            }
        }



        stage('Docker Build') {
            steps {
                sh 'docker build -t $DOCKER_REGISTRY/api-gateway:latest backend/api-gateway'
                sh 'docker build -t $DOCKER_REGISTRY/search-service:latest backend/searchAndDiscover'
                sh 'docker build -t $DOCKER_REGISTRY/feed-service:latest backend/feedAndTimeline'
            }
        }
    }
}