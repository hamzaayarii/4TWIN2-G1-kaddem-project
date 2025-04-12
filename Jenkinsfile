pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3'
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
        DOCKER_AVAILABLE = false  // Set this to true once Docker is installed
        EMAIL_NOTIFICATION = false  // Set this to true once email is configured
    }
    stages {
        // Stage 1: Build
        stage('Build') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -s $MAVEN_SETTINGS clean install'
                }
            }
        }
        // Stage 2: Test
        stage('Test') {
            steps {
                configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -s $MAVEN_SETTINGS test'
                }
            }
        }
        // Stage 3: SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                        sh 'mvn -s $MAVEN_SETTINGS sonar:sonar'
                    }
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
            when {
                expression { return env.DOCKER_AVAILABLE.toBoolean() }
            }
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}", ".")
                }
            }
        }
        // Stage 6: Push to Docker Hub
        stage('Push to Docker Hub') {
            when {
                expression { return env.DOCKER_AVAILABLE.toBoolean() }
            }
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}").push()
                    }
                }
            }
        }
        // Stage 7: Deploy
        stage('Deploy with Docker Compose') {
            when {
                expression { return env.DOCKER_AVAILABLE.toBoolean() }
            }
            steps {
                sh '''
                    # Force stop and remove all containers
                    docker-compose down --rmi all --volumes --remove-orphans --timeout 1 || true
                    
                    # Additional cleanup for any lingering containers
                    docker rm -f $(docker ps -aq --filter name=kaddem) || true
                    
                    # Small delay to ensure cleanup completes
                    sleep 5
                    
                    # Build and start fresh
                    docker-compose up -d --build --force-recreate
                '''
            }
        }
    }
    // Notifications & Cleanup
    post {
        success {
            script {
                if (env.EMAIL_NOTIFICATION.toBoolean()) {
                    emailext (
                        to: 'lazzezmed@gmail.com',
                        subject: "✅ Pipeline SUCCESS: ${currentBuild.fullDisplayName}",
                        body: """
                            <h2>Pipeline Execution Successful!</h2>
                            <p>Project: ${env.JOB_NAME}</p>
                            <p>Build Number: ${env.BUILD_NUMBER}</p>
                            <p>Duration: ${currentBuild.durationString}</p>
                            <p>Build URL: <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                        """,
                        mimeType: 'text/html'
                    )
                }
            }
        }
        failure {
            script {
                if (env.EMAIL_NOTIFICATION.toBoolean()) {
                    emailext (
                        to: 'lazzezmed@gmail.com',
                        subject: "❌ Pipeline FAILED: ${currentBuild.fullDisplayName}",
                        body: """
                            <h2>Pipeline Execution Failed</h2>
                            <p>Project: ${env.JOB_NAME}</p>
                            <p>Build Number: ${env.BUILD_NUMBER}</p>
                            <p>Duration: ${currentBuild.durationString}</p>
                            <p>Build URL: <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                            <h3>Console Output:</h3>
                            <pre>${currentBuild.rawBuild.getLog(100).join('\n')}</pre>
                        """,
                        mimeType: 'text/html',
                        attachLog: true
                    )
                }
            }
        }
        cleanup {
            script {
                if (env.DOCKER_AVAILABLE.toBoolean()) {
                    sh 'docker-compose down || true'
                }
            }
        }
    }
}
