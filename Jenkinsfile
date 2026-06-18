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
}