pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 60, unit: 'MINUTES')
    }

    parameters {
        booleanParam(name: 'PUSH_IMAGES', defaultValue: true, description: 'Push Docker images.')
        booleanParam(name: 'DEPLOY_TO_EKS', defaultValue: false, description: 'Future EKS deployment.')
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
                stage('API Gateway Tests') {
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

                stage('Search Service Tests') {
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

                stage('Feed Service Tests') {
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

                stage('Post Service Tests') {
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

                stage('JobPosting Service Tests') {
                    steps {
                        dir('backend/jobPosting') {
                            sh 'chmod +x mvnw && ./mvnw clean test ${MAVEN_CLI_OPTS}'
                        }
                    }
                    post {
                        always {
                            junit testResults: 'backend/jobPosting/target/surefire-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }

                stage('Notification Service Tests') {
                    steps {
                        dir('backend/notification') {
                            sh 'chmod +x mvnw && ./mvnw clean test ${MAVEN_CLI_OPTS}'
                        }
                    }
                    post {
                        always {
                            junit testResults: 'backend/notification/target/surefire-reports/*.xml', allowEmptyResults: true
                        }
                    }
                }
            }
        }

        stage('Frontend Tests And Build') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'CI=false npm test -- --watchAll=false'
                    sh 'CI=false npm run build'
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

                stage('Package UserProfile Service') {
                    steps {
                        dir('backend/userprofile') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Package JobPosting Service') {
                    steps {
                        dir('backend/jobPosting') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Package Notification Service') {
                    steps {
                        dir('backend/notification') {
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
                    env.JOBPOSTING_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/jobposting-service:${IMAGE_TAG}"
                    env.NOTIFICATION_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/notification-service:${IMAGE_TAG}"
                    env.FRONTEND_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/linkedin-frontend:${IMAGE_TAG}"
                }

                sh 'docker build -t ${API_GATEWAY_IMAGE} backend/api-gateway'
                sh 'docker build -t ${SEARCH_SERVICE_IMAGE} backend/searchAndDiscover'
                sh 'docker build -t ${FEED_SERVICE_IMAGE} backend/feedAndTimeline'
                sh 'docker build -t ${POST_SERVICE_IMAGE} backend/postAndTimeline'
                sh 'docker build -t ${USERPROFILE_SERVICE_IMAGE} backend/userprofile'
                sh 'docker build -t ${JOBPOSTING_SERVICE_IMAGE} backend/jobPosting'
                sh 'docker build -t ${NOTIFICATION_SERVICE_IMAGE} backend/notification'
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
                    sh 'docker push ${JOBPOSTING_SERVICE_IMAGE}'
                    sh 'docker push ${NOTIFICATION_SERVICE_IMAGE}'
                    sh 'docker push ${FRONTEND_IMAGE}'

                    sh 'docker tag ${API_GATEWAY_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/api-gateway:latest'
                    sh 'docker tag ${SEARCH_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/search-service:latest'
                    sh 'docker tag ${FEED_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/feed-service:latest'
                    sh 'docker tag ${POST_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/post-service:latest'
                    sh 'docker tag ${USERPROFILE_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/userprofile-service:latest'
                    sh 'docker tag ${JOBPOSTING_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/jobposting-service:latest'
                    sh 'docker tag ${NOTIFICATION_SERVICE_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/notification-service:latest'
                    sh 'docker tag ${FRONTEND_IMAGE} ${REGISTRY}/${DOCKER_NAMESPACE}/linkedin-frontend:latest'

                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/api-gateway:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/search-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/feed-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/post-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/userprofile-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/jobposting-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/notification-service:latest'
                    sh 'docker push ${REGISTRY}/${DOCKER_NAMESPACE}/linkedin-frontend:latest'
                }
            }
        }

        stage('Update GitOps Repository') {
            when {
                expression {
                    return params.PUSH_IMAGES && env.IS_MAIN_BRANCH == 'true'
                }
            }

            steps {
                withCredentials([
                    string(credentialsId: 'gitops-pat', variable: 'GITHUB_TOKEN')
                ]) {

                    sh '''
                    rm -rf gitops

                    git clone https://${GITHUB_TOKEN}@github.com/shubhra-tripathi/linkedin-clone-gitops.git gitops

                    cd gitops

                    git config user.name "Jenkins CI"
                    git config user.email "jenkins@linkedin-clone.local"

                    sed -i "s|image: shubhratripathi16/api-gateway:.*|image: shubhratripathi16/api-gateway:${IMAGE_TAG}|g" apps/api-gateway/deployment.yaml

                    sed -i "s|image: shubhratripathi16/search-service:.*|image: shubhratripathi16/search-service:${IMAGE_TAG}|g" apps/search-service/deployment.yaml

                    sed -i "s|image: shubhratripathi16/feed-service:.*|image: shubhratripathi16/feed-service:${IMAGE_TAG}|g" apps/feed-service/deployment.yaml

                    sed -i "s|image: shubhratripathi16/post-service:.*|image: shubhratripathi16/post-service:${IMAGE_TAG}|g" apps/post-service/deployment.yaml

                    sed -i "s|image: shubhratripathi16/userprofile-service:.*|image: shubhratripathi16/userprofile-service:${IMAGE_TAG}|g" apps/userprofile-service/deployment.yaml

                    sed -i "s|image: shubhratripathi16/jobposting-service:.*|image: shubhratripathi16/jobposting-service:${IMAGE_TAG}|g" apps/jobposting-service/deployment.yaml

                    sed -i "s|image: shubhratripathi16/notification-service:.*|image: shubhratripathi16/notification-service:${IMAGE_TAG}|g" apps/notification-service/deployment.yaml

                    sed -i "s|image: shubhratripathi16/linkedin-frontend:.*|image: shubhratripathi16/linkedin-frontend:${IMAGE_TAG}|g" apps/frontend/deployment.yaml

                    git add .

                    git commit -m "Deploy build ${BUILD_NUMBER} (${IMAGE_TAG})" || true

                    git push origin main
                    '''
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
