pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3'
        // Use the exact name of your JDK installation from Jenkins Global Tool Configuration
        jdk 'jdk-17' // or 'Java_17' depending on your Jenkins configuration
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
        NEXUS_REPO = "http://192.168.56.10:8081/repository/maven-releases/"
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=4TWIN2-G1-kaddem-project'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests -DaltDeploymentRepository=deploymentRepo::default::${NEXUS_REPO}'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    // Ensure Docker is available
                    sh 'docker --version'
                    
                    // Login with credentials
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerr',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            echo \"${DOCKER_PASS}\" | docker login -u \"${DOCKER_USER}\" --password-stdin
                            docker build -t ${DOCKER_IMAGE}:${env.BUILD_NUMBER} .
                            docker push ${DOCKER_IMAGE}:${env.BUILD_NUMBER}
                        """
                    }
                }
            }
        }
    }
    post {
        always {
            emailext (
                to: 'lazzezmed@gmail.com',
                subject: "Build ${currentBuild.currentResult}: Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: """
                    <p>Build: <b>${env.JOB_NAME} - #${env.BUILD_NUMBER}</b></p>
                    <p>Status: <b style="color:${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'}">${currentBuild.currentResult}</b></p>
                    <p>Console: <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                """,
                attachLog: true,
                mimeType: 'text/html'
            )
        }
    }
}
