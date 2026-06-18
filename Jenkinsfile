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
    }
}