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
    }
}