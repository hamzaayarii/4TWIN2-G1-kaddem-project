pipeline {
    agent {
        docker {
            image 'maven:3.9.3-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    tools {
        maven 'Maven-3.9.3'
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
        DOCKER_AVAILABLE = true
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
        SONAR_PROJECT_KEY = "4TWIN2-G1-kaddem"
        MYSQL_STARTUP_TIMEOUT = 45
        APP_STARTUP_TIMEOUT = 30
        // Network and container names
        DOCKER_NETWORK = "kaddem-network"
        MYSQL_CONTAINER = "mysql"
        APP_CONTAINER = "kaddem-app"
    }
    stages {
        stage('Setup') {
            steps {
                sh '''
                    apt-get update
                    apt-get install -y apt-transport-https ca-certificates curl gnupg2 software-properties-common
                    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
                    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
                    apt-get update
                    apt-get install -y docker-ce-cli
                '''
            }
        }
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
                        -Dsonar.host.url=http://localhost:9000
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
        stage('Deploy MySQL') {
            steps {
                script {
                    sh """
                        docker network create ${DOCKER_NETWORK} || true
                        docker volume create mysql_data || true
                        docker stop ${MYSQL_CONTAINER} || true
                        docker rm ${MYSQL_CONTAINER} || true
                        docker run -d --name ${MYSQL_CONTAINER} \
                            --network ${DOCKER_NETWORK} \
                            --health-cmd='mysqladmin ping -h localhost' \
                            --health-interval=10s \
                            --health-timeout=5s \
                            --health-retries=3 \
                            -v mysql_data:/var/lib/mysql \
                            -e MYSQL_ROOT_PASSWORD=root \
                            -e MYSQL_DATABASE=kaddem \
                            mysql:8.0
                        
                        echo 'Waiting for MySQL to be ready...'
                        timeout=${MYSQL_STARTUP_TIMEOUT}
                        until docker exec ${MYSQL_CONTAINER} mysqladmin ping -h localhost -u root --password=root --silent || [ \$timeout -le 0 ]; do
                            sleep 5
                            timeout=\$((timeout-5))
                            echo "Waiting for MySQL... \$timeout seconds remaining"
                        done
                        
                        if [ \$timeout -le 0 ]; then
                            echo "MySQL failed to start within timeout"
                            exit 1
                        fi
                        
                        echo "MySQL is ready!"
                    """
                }
            }
        }
        // Stage 7: Deploy
        stage('Deploy with Docker') {
            steps {
                script {
                    sh """
                        docker network create ${DOCKER_NETWORK} || true
                        docker stop ${APP_CONTAINER} || true
                        docker rm ${APP_CONTAINER} || true
                        docker run -d --name ${APP_CONTAINER} \
                            --network ${DOCKER_NETWORK} \
                            -p 8089:8089 \
                            -e SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_CONTAINER}:3306/kaddem?createDatabaseIfNotExist=true \
                            -e SPRING_DATASOURCE_USERNAME=root \
                            -e SPRING_DATASOURCE_PASSWORD=root \
                            ${DOCKER_IMAGE}:${BUILD_NUMBER}
                        
                        echo "Waiting for application to start..."
                        timeout=${APP_STARTUP_TIMEOUT}
                        until curl -s http://localhost:8089/actuator/health || [ \$timeout -le 0 ]; do
                            sleep 5
                            timeout=\$((timeout-5))
                            echo "Waiting for application... \$timeout seconds remaining"
                        done
                        
                        if [ \$timeout -le 0 ]; then
                            echo "Application failed to start within timeout"
                            exit 1
                        fi
                        
                        echo "Application is ready!"
                    """
                }
            }
        }
        // Stage 8: Cleanup
        stage('Cleanup') {
            steps {
                script {
                    sh """
                        docker stop ${APP_CONTAINER} ${MYSQL_CONTAINER} || true
                        docker rm ${APP_CONTAINER} ${MYSQL_CONTAINER} || true
                        docker rmi ${DOCKER_IMAGE}:${BUILD_NUMBER} || true
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
                sh """
                    docker stop ${APP_CONTAINER} ${MYSQL_CONTAINER} || true
                    docker rm ${APP_CONTAINER} ${MYSQL_CONTAINER} || true
                    docker network rm ${DOCKER_NETWORK} || true
                """
            }
        }
    }
}
