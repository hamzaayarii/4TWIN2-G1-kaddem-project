
pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') // Create this in Jenkins
        IMAGE_NAME = "hamzabox/kaddem-devops"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }
    stages {
        stage('GIT') {
            steps {
                git branch: 'AyariHamza-4TWIN2-G1',
                    url: 'https://github.com/hamzaayarii/4TWIN2-G1-kaddem-project.git'
            }
        }

        stage('Compile Stage') {
            steps {
                sh 'mvn clean compile'
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

 stage('Deploy to Nexus') {
            steps {
                script {
                    sh """
                    mvn deploy -DaltDeploymentRepository=deploymentRepo::default::http://192.168.33.10:8083/repository/maven-snapshots/
                    """
                }
            }
        }


         stage('Build Docker Image') {
                     steps {
                         sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                         // Also tag as latest for convenience
                         sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                     }
                 }

                 stage('Push to Docker Hub') {
                     steps {
                         script {
                             // Login to Docker Hub
                             sh "echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin"

                             // Push the images
                             sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                             sh "docker push ${IMAGE_NAME}:latest"
                         }
                     }
                 }

                 stage('Deploy with Docker Compose') {
                     steps {
                         // Update the docker-compose.yml to use the new image
                         sh """
                         sed -i 's|image: hamzabox/kaddem-devops:.*|image: ${IMAGE_NAME}:${IMAGE_TAG}|' docker-compose.yml
                         """

                         // Deploy with docker-compose
                         sh 'docker compose down'
                         sh 'docker compose up -d'
                     }
                 }
             }

             post {
                 always {
                     // Clean up
                     sh 'docker logout'
                     // Clean workspace
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