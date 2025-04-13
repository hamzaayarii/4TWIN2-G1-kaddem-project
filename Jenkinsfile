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
                sh '''
                    # Stop any existing containers with our app name pattern
                    echo "Stopping existing containers..."
                    docker ps -q --filter name=kaddem | xargs -r docker stop || true
                    docker ps -aq --filter name=kaddem | xargs -r docker rm -f || true
                    
                    # Remove old images to prevent 'image in use' errors
                    echo "Cleaning up old images..."
                    docker images | grep 'kaddem' | awk '{print $3}' | xargs -r docker rmi -f || true
                    
                    # Careful network cleanup
                    echo "Cleaning up Docker networks..."
                    docker network ls | grep "kaddem" | awk '{print $1}' | xargs -r docker network rm || true
                    docker network prune -f
                    
                    # Recreate network if needed
                    docker network create kaddem-network || true
                    
                    # Remove orphaned volumes
                    echo "Cleaning up volumes..."
                    docker volume prune -f
                    
                    # Small delay to ensure cleanup completes
                    echo "Waiting for cleanup to complete..."
                    sleep 5
                    
                    # Start fresh with new configuration
                    echo "Starting new containers..."
                    docker compose up -d --force-recreate --remove-orphans
                    
                    # Wait for health checks with optimized timing
                    echo "Waiting for containers to be healthy..."
                    MAX_ATTEMPTS=8  # Reduced from 30 to 8 attempts
                    SLEEP_DURATION=3  # Slightly increased from 2 to 3 seconds for better stability
                    
                    for i in $(seq 1 $MAX_ATTEMPTS); do
                        echo "Health check attempt $i/$MAX_ATTEMPTS..."
                        
                        # Check if containers exist and are healthy
                        if docker ps --format '{{.Names}} {{.Status}}' | grep -E 'kaddem-(app|mysql).*healthy' | wc -l | grep -q 2; then
                            echo "✅ All containers are healthy!"
                            echo "Current container status:"
                            docker ps --format 'table {{.Names}}\t{{.Status}}'
                            exit 0
                        fi
                        
                        # If we're on the last attempt, show detailed status
                        if [ $i -eq $MAX_ATTEMPTS ]; then
                            echo "❌ Timeout waiting for containers to be healthy"
                            echo "Current container status:"
                            docker ps --format 'table {{.Names}}\t{{.Status}}'
                            docker logs kaddem-app || true
                            docker logs kaddem-mysql || true
                            exit 1
                        fi
                        
                        echo "Waiting ${SLEEP_DURATION} seconds before next check..."
                        sleep $SLEEP_DURATION
                    done
                '''
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
            // Only clean up containers on failure
            sh '''
                docker compose down --rmi all --volumes --remove-orphans || true
                docker ps -aq --filter name=kaddem | xargs -r docker rm -f || true
            '''
        }
        always {
            sh 'docker logout'
        }
    }
}
