pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 60, unit: 'MINUTES')
    }

    parameters {
        booleanParam(
            name: 'PUSH_IMAGES',
            defaultValue: true,
            description: 'Push Docker images when building the main branch.'
        )
        booleanParam(
            name: 'DEPLOY_TO_EKS',
            defaultValue: false,
            description: 'Reserved for a future Kubernetes/EKS deployment stage.'
        )
    }

    environment {
        REGISTRY = 'docker.io'
        DOCKER_NAMESPACE = 'shubhratripathi16'
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
        MAVEN_CLI_OPTS = '--batch-mode --errors --fail-at-end --no-transfer-progress'
        NODE_ENV = 'test'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_SHA = sh(script: 'git rev-parse --short=12 HEAD', returnStdout: true).trim()
                    env.SAFE_BRANCH_NAME = env.BRANCH_NAME ? env.BRANCH_NAME.replaceAll('[^A-Za-z0-9_.-]', '-') : 'local'
                    env.IMAGE_TAG = "${env.SAFE_BRANCH_NAME}-${env.BUILD_NUMBER}-${env.GIT_SHA}"
                    env.IS_MAIN_BRANCH = (env.BRANCH_NAME == null || env.BRANCH_NAME == 'main') ? 'true' : 'false'
                }
            }
        }

        stage('Backend Tests') {
            parallel {
                stage('API Gateway') {
                    steps {
                        dir('backend/api-gateway') {
                            sh 'chmod +x mvnw && ./mvnw clean test ${MAVEN_CLI_OPTS}'
                        }
                    }
                    post {
                        always {
                            junit testResults: 'backend/api-gateway/target/surefire-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }

                stage('Search Service') {
                    steps {
                        dir('backend/searchAndDiscover') {
                            sh 'chmod +x mvnw && ./mvnw clean test ${MAVEN_CLI_OPTS}'
                        }
                    }
                    post {
                        always {
                            junit testResults: 'backend/searchAndDiscover/target/surefire-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }

                stage('Feed Service') {
                    steps {
                        dir('backend/feedAndTimeline') {
                            sh 'chmod +x mvnw && ./mvnw clean test ${MAVEN_CLI_OPTS}'
                        }
                    }
                    post {
                        always {
                            junit testResults: 'backend/feedAndTimeline/target/surefire-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }

                stage('Post Service') {
                    steps {
                        dir('backend/postAndTimeline') {
                            sh 'chmod +x mvnw && ./mvnw clean test ${MAVEN_CLI_OPTS}'
                        }
                    }
                    post {
                        always {
                            junit testResults: 'backend/postAndTimeline/target/surefire-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }

                stage('User Profile Service') {
                    steps {
                        dir('backend/userprofile') {
                            sh 'chmod +x mvnw && ./mvnw clean test ${MAVEN_CLI_OPTS}'
                        }
                    }
                    post {
                        always {
                            junit testResults: 'backend/userprofile/target/surefire-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }
            }
        }

        stage('Frontend Tests And Build') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'CI=true npm test -- --watch=false'
                    sh 'npm run build'
                }
            }
        }

        stage('Package Services') {
            parallel {
                stage('Package API Gateway') {
                    steps {
                        dir('backend/api-gateway') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Package Search Service') {
                    steps {
                        dir('backend/searchAndDiscover') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Package Feed Service') {
                    steps {
                        dir('backend/feedAndTimeline') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Package Post Service') {
                    steps {
                        dir('backend/postAndTimeline') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Package User Profile Service') {
                    steps {
                        dir('backend/userprofile') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    env.API_GATEWAY_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/api-gateway:${IMAGE_TAG}"
                    env.SEARCH_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/search-service:${IMAGE_TAG}"
                    env.FEED_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/feed-service:${IMAGE_TAG}"
                    env.POST_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/post-service:${IMAGE_TAG}"
                    env.USERPROFILE_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/userprofile-service:${IMAGE_TAG}"
                    env.FRONTEND_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/linkedin-frontend:${IMAGE_TAG}"
                }

                sh 'docker build -t ${API_GATEWAY_IMAGE} backend/api-gateway'
                sh 'docker build -t ${SEARCH_SERVICE_IMAGE} backend/searchAndDiscover'
                sh 'docker build -t ${FEED_SERVICE_IMAGE} backend/feedAndTimeline'
                sh 'docker build -t ${POST_SERVICE_IMAGE} backend/postAndTimeline'
                sh 'docker build -t ${USERPROFILE_SERVICE_IMAGE} backend/userprofile'
                sh 'docker build -t ${FRONTEND_IMAGE} frontend'
            }
        }

        stage('Push Docker Images') {
            when {
                expression {
                    return params.PUSH_IMAGES && env.IS_MAIN_BRANCH == 'true'
                }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin ${REGISTRY}'
                    sh 'docker push ${API_GATEWAY_IMAGE}'
                    sh 'docker push ${SEARCH_SERVICE_IMAGE}'
                    sh 'docker push ${FEED_SERVICE_IMAGE}'
                    sh 'docker push ${POST_SERVICE_IMAGE}'
                    sh 'docker push ${USERPROFILE_SERVICE_IMAGE}'
                    sh 'docker push ${FRONTEND_IMAGE}'

                    sh 'docker tag ${API_GATEWAY_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/api-gateway:latest'
                    sh 'docker tag ${SEARCH_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/search-service:latest'
                    sh 'docker tag ${FEED_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/feed-service:latest'
                    sh 'docker tag ${POST_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/post-service:latest'
                    sh 'docker tag ${USERPROFILE_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/userprofile-service:latest'
                    sh 'docker tag ${FRONTEND_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/linkedin-frontend:latest'

                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/api-gateway:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/search-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/feed-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/post-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/userprofile-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/linkedin-frontend:latest'
                }
            }
        }

        stage('Deploy To EKS') {
            when {
                expression {
                    return params.DEPLOY_TO_EKS
                }
            }
            steps {
                script {
                    if (!fileExists('k8s') && !fileExists('helm')) {
                        error('DEPLOY_TO_EKS was requested, but no Kubernetes manifests or Helm charts exist in this repository yet.')
                    }
                }
                echo 'Add your kubectl or Helm deployment commands here once the Kubernetes deployment assets are in the repository.'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'backend/**/target/*.jar, frontend/build/**', allowEmptyArchive: true
            sh 'docker logout ${REGISTRY} || true'
        }
        success {
            echo "Pipeline completed successfully. Image tag: ${IMAGE_TAG}"
        }
        failure {
            echo 'Pipeline failed. Check the failing stage before attempting deployment.'
        }
    }
}
