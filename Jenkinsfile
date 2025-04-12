pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3'
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
        DOCKER_AVAILABLE = true
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
    }
    stages {
        // Stage 1: Build
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        // Stage 2: Test
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        // Stage 3: SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        // Stage 4: Nexus Deploy
        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
        // Stage 5: Docker Build
        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                }
            }
        }
        // Stage 6: Push to Docker Hub
        stage('Push to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                    }
                }
            }
        }
        // Stage 7: Deploy
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    sh """
                        docker-compose down --rmi all --volumes --remove-orphans || true
                        docker rm -f kaddem || true
                        sleep 5
                        docker-compose up -d --build --force-recreate
                    """
                }
            }
        }
    }
    // Notifications & Cleanup
    post {
        success {
            mail(
                to: 'lazzezmed@gmail.com',
                subject: "✅ SUCCESS: Pipeline ${currentBuild.fullDisplayName}",
                body: """Pipeline execution successful!
                    |
                    |Job: ${env.JOB_NAME}
                    |Build: ${env.BUILD_NUMBER}
                    |Duration: ${currentBuild.durationString}
                    |
                    |Check details at: ${env.BUILD_URL}
                    """.stripMargin()
            )
        }
        failure {
            mail(
                to: 'lazzezmed@gmail.com',
                subject: "❌ FAILED: Pipeline ${currentBuild.fullDisplayName}",
                body: """Pipeline execution failed!
                    |
                    |Job: ${env.JOB_NAME}
                    |Build: ${env.BUILD_NUMBER}
                    |Duration: ${currentBuild.durationString}
                    |
                    |Check details at: ${env.BUILD_URL}
                    """.stripMargin()
            )
        }
        cleanup {
            script {
                if (env.DOCKER_AVAILABLE.toBoolean()) {
                    sh "docker-compose down || true"
                }
            }
        }
    }
}
