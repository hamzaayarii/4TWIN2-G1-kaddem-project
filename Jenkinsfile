// This pipeline implements CI/CD workflow with GitHub webhook integration
// Version: 1.0.0 - Webhook Testing
pipeline {
    agent any
    triggers {
        githubPush()
    }
    tools {
        maven 'Maven-3.9.3'
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
        APP_URL = "http://localhost:8089"
        JENKINS_URL = "http://localhost:8080"
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
        // Stage 5: Docker Login
        stage('Docker Login') {
            steps {
                sh '''
                    # Clean up any stale networks first
                    echo "Cleaning up Docker networks..."
                    docker network prune -f
                    
                    # Create our network if it doesn't exist
                    docker network create devops_net || true
                    
                    # Try Docker login
                    echo "Attempting Docker login..."
                    echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin
                '''
            }
        }
        // Stage 6: Docker Build
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
            }
        }
        // Stage 7: Push to Docker Hub
        stage('Push to Docker Hub') {
            steps {
                sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                sh "docker push ${DOCKER_IMAGE}:latest"
            }
        }
        // Stage 8: Deploy
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    // Check if MySQL is running and healthy
                    sh '''
                        echo "Checking MySQL container status..."
                        if docker ps -q --filter name=kaddem-mysql | grep -q .; then
                            echo "MySQL container is already running"
                        else
                            echo "Starting MySQL container..."
                            docker compose up -d db
                            echo "Waiting for MySQL to be healthy..."
                            attempt=1
                            # MySQL typically needs 5-10 seconds to start
                            while [ $attempt -le 10 ]; do
                                if docker ps --filter name=kaddem-mysql --filter health=healthy -q | grep -q .; then
                                    echo "MySQL is healthy!"
                                    break
                                fi
                                echo "Waiting for MySQL to be ready... ($attempt/10)"
                                sleep 2
                                attempt=$((attempt + 1))
                            done
                        fi
                    '''

                    // Only rebuild and restart the application container
                    sh '''
                        echo "Rebuilding and restarting application..."
                        docker compose up -d --build --no-deps app frontend
                        
                        echo "Waiting for application to start..."
                        attempt=1
                        # Spring Boot typically starts in 10-15 seconds
                        while [ $attempt -le 12 ]; do
                            if docker ps --filter name=kaddem-app -q | grep -q .; then
                                # Add a quick check for Spring Boot readiness
                                if curl -s http://localhost:8089/kaddem/actuator/health | grep -q '"status":"UP"'; then
                                    echo "Application is up and healthy!"
                                    break
                                fi
                            fi
                            echo "Waiting for application to start... ($attempt/12)"
                            sleep 3
                            attempt=$((attempt + 1))
                        done
                    '''
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
                    |Application is running at: ${APP_URL}
                    |Jenkins is accessible at: ${JENKINS_URL}
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
            // Don't remove everything on failure, just stop the app containers
            sh '''
                echo "Stopping application containers..."
                docker compose stop app frontend || true
                docker compose rm -f app frontend || true
            '''
        }
        always {
            sh 'docker logout'
        }
    }
}
