pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') // Create this in Jenkins

        BACKEND_IMAGE_NAME = "hamzabox/kaddem-devops"
        BACKEND_IMAGE_TAG = "${BUILD_NUMBER}"

        FRONTEND_IMAGE_NAME = "hamzabox/kaddem-frontend"
        FRONTEND_IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Repositories') {
                    steps {
                        script {
                            // Create separate directories for each repository
                            sh 'mkdir -p backend frontend'

                            // Checkout backend
                            dir('backend') {
                                git branch: 'AyariHamza-4TWIN2-G1',
                                    url: 'https://github.com/hamzaayarii/4TWIN2-G1-kaddem-project.git'
                            }

                            // Checkout frontend
                            dir('frontend') {
                                git branch: 'pre-prod',
                                    url: 'https://github.com/hamzaayarii/devops-kaddem-frontend.git'
                            }
                        }
                    }
                }


        stage('Backend - Compile') {
                   steps {
                       dir('backend') {
                           sh 'mvn clean compile'
                       }
                   }
               }

        stage('Frontend - Install Dependencies') {
                   steps {
                       dir('frontend') {
                           sh 'npm install'
                       }
                   }
               }


        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'scanner'
                    withSonarQubeEnv() {
                        sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=kaddem-devops \
                        -Dsonar.projectName='Kaddem DevOps Project' \
                        -Dsonar.sources=src/main \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.scm.provider=git
                        """
                    }
                }
            }
        }

        stage('Frontend - Run Tests') {
            steps {
             dir('frontend') {

                sh 'npm test'
            }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                 dir('backend') {

                    sh """
                    mvn deploy -DaltDeploymentRepository=deploymentRepo::default::http://192.168.33.10:8083/repository/maven-snapshots/
                    """
                }
                }
            }
        }

        stage('Backend - Build Docker Image') {
            steps {
             dir('backend') {

                sh "docker build -t ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG} ."
                sh "docker tag ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG} ${BACKEND_IMAGE_NAME}:latest"
            }
            }
        }

        stage('Frontend - Build Docker Image') {
            steps {
            dir('frontend') {
                sh "docker build -t ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG} ."
                sh "docker tag ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG} ${FRONTEND_IMAGE_NAME}:latest"
            }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"

                    // Push Backend Images
                    dir('backend') {
                    sh "docker push ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG}"
                    sh "docker push ${BACKEND_IMAGE_NAME}:latest"
}
dir('frontend') {
                    // Push Frontend Images
                    sh "docker push ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG}"
                    sh "docker push ${FRONTEND_IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                // Update docker-compose.yml
                sh """
                sed -i 's|image: hamzabox/kaddem-devops:.*|image: ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG}|' docker-compose.yml
                sed -i 's|image: hamzabox/kaddem-frontend:.*|image: ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG}|' docker-compose.yml
                """

                // Deploy with docker-compose
                sh 'docker compose down'
                sh 'docker compose up -d'
            }
        }
    }

    post {
        always {
            sh 'docker logout'
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
        }
    }
}
