pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
        nodejs 'NodeJS_22'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') // Create this in Jenkins

        BACKEND_IMAGE_NAME = "hamzabox/kaddem-devops"
        BACKEND_IMAGE_TAG = "${BUILD_NUMBER}"

        FRONTEND_IMAGE_NAME = "hamzabox/kaddem-frontend"
        FRONTEND_IMAGE_TAG = "${BUILD_NUMBER}"

        // Local registry info
        LOCAL_REGISTRY = "localhost:5000"
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

        stage('Backend - Compile & Unit Tests') {
            steps {
                dir('backend') {
                    sh 'mvn clean compile'
                    sh 'mvn test'
                }
            }
        }

        stage('Backend - SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'scanner'
                    withSonarQubeEnv('SonarQube') {
                        dir('backend') {
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
        }

        stage('Frontend - Install Dependencies') {
            steps {
                dir('frontend') {
                    sh '''
                        npm install
                    '''
                }
            }
        }

        stage('Frontend - Run Tests') {
            steps {
                dir('frontend') {
                    sh '''
                        npm test
                    '''
                }
            }
        }
/*
        stage('Frontend - SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'scanner'
                    withSonarQubeEnv('SonarQube') {
                        dir('frontend') {
                            sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=kaddem-frontend \
                            -Dsonar.projectName='Kaddem Frontend Project' \
                            -Dsonar.sources=src \
                            -Dsonar.language=js \
                            -Dsonar.sourceEncoding=UTF-8 \
                            -Dsonar.tests=src \
                            -Dsonar.javascript.lcov.reportPaths=coverage/lcov-report/index-lcov-report.json
                            """
                        }
                    }
                }
            }
        }
*/
        stage('Deploy JAR to Nexus') {
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
                    // Tag for local registry
                    sh "docker tag ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG} ${LOCAL_REGISTRY}/${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG}"
                }
            }
        }

        stage('Frontend - Build Docker Image') {
            steps {
                dir('frontend') {
                    sh "docker build -t ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG} ."
                    sh "docker tag ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG} ${FRONTEND_IMAGE_NAME}:latest"
                    // Tag for local registry
                    sh "docker tag ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG} ${LOCAL_REGISTRY}/${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG}"
                }
            }
        }

        stage('Push Images to Local Registry') {
            steps {
                script {
                    // Push backend image to local registry
                    sh "docker push ${LOCAL_REGISTRY}/${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG}"
                    sh "docker push ${LOCAL_REGISTRY}/${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG}"
                }
            }
        }
/*
        stage('Push to Docker Hub') {
            steps {
                script {
                    sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"

                    sh "docker push ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG}"
                    sh "docker push ${BACKEND_IMAGE_NAME}:latest"
                    sh "docker push ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG}"
                    sh "docker push ${FRONTEND_IMAGE_NAME}:latest"
                }
            }
        }
*/
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Update image references in existing docker-compose.yml
                    sh """
                    # Update the image references in docker-compose.yml
                    sed -i 's|image: hamzabox/kaddem-devops:.*|image: ${BACKEND_IMAGE_NAME}:${BACKEND_IMAGE_TAG}|' docker-compose.yml
                    sed -i 's|image: hamzabox/kaddem-frontend:.*|image: ${FRONTEND_IMAGE_NAME}:${FRONTEND_IMAGE_TAG}|' docker-compose.yml

                    # Stop previous containers if running
                    docker-compose down || true

                    # Start containers with new images
                    docker-compose up -d
                    """
                }
            }
        }

        stage('Verify Containers') {
            steps {
                script {
                    // Check if containers are running
                    sh '''
                    echo "Verifying containers..."
                    docker ps | grep kaddem
                    '''
                }
            }
        }
    }

    post {
        always {
            script {
                currentBuild.result = currentBuild.currentResult
            }

            emailext(
                subject: "Pipeline Status ${currentBuild.result}: ${env.JOB_NAME}",
                body: """<html>
                           <body>
                               <p>Dear Team,</p>
                               <p>The pipeline for project <strong>${env.JOB_NAME}</strong> has completed with the status: <strong>${currentBuild.result}</strong>.</p>
                               <p>Thank you,</p>
                               <p>Your Jenkins Server</p>
                           </body>
                       </html>""",
                to: 'hamzosayari07@gmail.com',
                from: 'hamzosayari07@gmail.com',
                replyTo: 'hamzosayari07@gmail.com',
                mimeType: 'text/html'
            )
        }
    }
}