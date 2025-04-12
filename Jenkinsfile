pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3'
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
        DOCKER_AVAILABLE = true
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
        SONAR_PROJECT_KEY = "4TWIN2-G1-kaddem"
        // Define network names from docker-compose
        DOCKER_NETWORK = "devops-tools_default"
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
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        // Stage 3: SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(installationName: 'SonarQube') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.projectName=${SONAR_PROJECT_KEY} \
                        -Dsonar.host.url=http://devops-tools-sonarqube-1:9000
                    """
                }
            }
        }
        // Stage 3.1: Quality Gate
        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        // Stage 4: Nexus Deploy
        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -s $MAVEN_SETTINGS deploy -DskipTests'
                }
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
