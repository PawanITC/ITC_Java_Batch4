pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 60, unit: 'MINUTES')
    }

    parameters {
        booleanParam(name: 'PUSH_IMAGES', defaultValue: true, description: 'Push Docker images and update GitOps repo.')
    }

    environment {
        REGISTRY = 'docker.io'
        DOCKER_NAMESPACE = 'shubhratripathi16'
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
        MAVEN_CLI_OPTS = '--batch-mode --errors --fail-at-end --no-transfer-progress'
        GITOPS_REPO = 'github.com/shubhra-tripathi/linkedin-clone-gitops.git'
        MSK_BOOTSTRAP_SERVERS = 'b-1.linkedinmsk.vnj8l2.c3.kafka.eu-west-2.amazonaws.com:9092,b-2.linkedinmsk.vnj8l2.c3.kafka.eu-west-2.amazonaws.com:9092'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_SHA = sh(script: 'git rev-parse --short=12 HEAD', returnStdout: true).trim()
                    env.SAFE_BRANCH_NAME = env.BRANCH_NAME ? env.BRANCH_NAME.replaceAll('[^A-Za-z0-9_.-]', '-') : 'main'
                    env.IMAGE_TAG = "${env.SAFE_BRANCH_NAME}-${env.BUILD_NUMBER}-${env.GIT_SHA}"
                    env.IS_MAIN_BRANCH = (env.BRANCH_NAME == null || env.BRANCH_NAME == 'main') ? 'true' : 'false'
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'CI=false npm run build'
                }
            }
        }

        stage('Package Services') {
            parallel {
                stage('API Gateway') {
                    steps {
                        dir('backend/api-gateway') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Search Service') {
                    steps {
                        dir('backend/searchAndDiscover') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Feed Service') {
                    steps {
                        dir('backend/feedAndTimeline') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Post Service') {
                    steps {
                        dir('backend/postAndTimeline') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('UserProfile Service') {
                    steps {
                        dir('backend/userprofile') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('Connections Service') {
                    steps {
                        dir('backend/connections-service') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }

                stage('JobPosting Service') {
                    steps {
                        dir('backend/jobPosting') {
                            sh 'chmod +x mvnw && ./mvnw clean package -DskipTests ${MAVEN_CLI_OPTS}'
                        }
                    }
                }


                stage('Notification Service') {
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
                    env.CONNECTIONS_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/connections-service:${IMAGE_TAG}"
                    env.JOBPOSTING_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/jobposting-service:${IMAGE_TAG}"
                    env.NOTIFICATION_SERVICE_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/notification-service:${IMAGE_TAG}"
                    env.FRONTEND_IMAGE = "${REGISTRY}/${DOCKER_NAMESPACE}/linkedin-frontend:${IMAGE_TAG}"
                }

                sh 'docker build -t ${API_GATEWAY_IMAGE} backend/api-gateway'
                sh 'docker build -t ${SEARCH_SERVICE_IMAGE} backend/searchAndDiscover'
                sh 'docker build -t ${FEED_SERVICE_IMAGE} backend/feedAndTimeline'
                sh 'docker build -t ${POST_SERVICE_IMAGE} backend/postAndTimeline'
                sh 'docker build -t ${USERPROFILE_SERVICE_IMAGE} backend/userprofile'
                sh 'docker build -t ${CONNECTIONS_SERVICE_IMAGE} backend/connections-service'
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
                    sh 'docker push ${CONNECTIONS_SERVICE_IMAGE}'
                    sh 'docker push ${JOBPOSTING_SERVICE_IMAGE}'
                    sh 'docker push ${NOTIFICATION_SERVICE_IMAGE}'
                    sh 'docker push ${FRONTEND_IMAGE}'
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

                    git clone https://${GITHUB_TOKEN}@${GITOPS_REPO} gitops

                    cd gitops

                    git config user.name "Jenkins CI"
                    git config user.email "jenkins@linkedin-clone.local"

                    sed -i "s|image: .*api-gateway:.*|image: docker.io/shubhratripathi16/api-gateway:${IMAGE_TAG}|g" environments/prod/api-gateway/deployment.yaml

                    sed -i "s|image: .*search-service:.*|image: docker.io/shubhratripathi16/search-service:${IMAGE_TAG}|g" environments/prod/search-service/deployment.yaml

                     sed -i "s|image: .*feed-service:.*|image: docker.io/shubhratripathi16/feed-service:${IMAGE_TAG}|g" environments/prod/feed-service/deployment.yaml

                     if grep -q "name: MSK_BOOTSTRAP_SERVERS" environments/prod/feed-service/deployment.yaml; then
                       sed -i "/name: MSK_BOOTSTRAP_SERVERS/{n;s|value:.*|value: ${MSK_BOOTSTRAP_SERVERS}|;}" environments/prod/feed-service/deployment.yaml
                     else
                       sed -i "/image: docker.io\\/shubhratripathi16\\/feed-service:${IMAGE_TAG}/a\\
        env:\\
        - name: MSK_BOOTSTRAP_SERVERS\\
          value: ${MSK_BOOTSTRAP_SERVERS}" environments/prod/feed-service/deployment.yaml
                     fi

                    sed -i "s|image: .*post-service:.*|image: docker.io/shubhratripathi16/post-service:${IMAGE_TAG}|g" environments/prod/post-service/deployment.yaml

                    sed -i "s|image: .*userprofile-service:.*|image: docker.io/shubhratripathi16/userprofile-service:${IMAGE_TAG}|g" environments/prod/userprofile-service/deployment.yaml

                    sed -i "s|image: .*connections-service:.*|image: docker.io/shubhratripathi16/connections-service:${IMAGE_TAG}|g" environments/prod/connections-service/deployment.yaml

                    sed -i "s|image: .*jobposting-service:.*|image: docker.io/shubhratripathi16/jobposting-service:${IMAGE_TAG}|g" environments/prod/jobposting-service/deployment.yaml

                    sed -i "s|image: .*notification-service:.*|image: docker.io/shubhratripathi16/notification-service:${IMAGE_TAG}|g" environments/prod/notification-service/deployment.yaml

                    sed -i "s|image: .*linkedin-frontend:.*|image: docker.io/shubhratripathi16/linkedin-frontend:${IMAGE_TAG}|g" environments/prod/frontend/deployment.yaml

                    git add environments/prod

                    git commit -m "Deploy application images ${IMAGE_TAG}" || true

                    git push origin main
                    '''
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'backend/**/target/*.jar, frontend/build/**', allowEmptyArchive: true
            sh 'docker logout ${REGISTRY} || true'
        }

        success {
            echo "Pipeline completed successfully. Argo CD will deploy image tag: ${IMAGE_TAG}"
        }

        failure {
            echo 'Pipeline failed. Check the failing stage.'
        }
    }
}
