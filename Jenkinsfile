pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3'
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
    }
    stages {
        // Stage 1: Build
        stage('Build') {
            steps { sh 'mvn clean install' }
        }
        // Stage 2: Test
        stage('Test') {
            steps { sh 'mvn test' }
            post { always { junit 'target/surefire-reports/*.xml' } }
        }
        // Stage 3: SonarQube
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=4TWIN2-G1-kaddem-project'
                }
            }
        }
        // Stage 4: Nexus Deploy
        stage('Deploy to Nexus') {
            steps { sh 'mvn deploy -DskipTests' }
        }
        // Stage 5: Docker Build
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}", ".")
                }
            }
        }
        // Stage 6: Push to Docker Hub
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerr') {
                        docker.image("${DOCKER_IMAGE}:${env.BUILD_NUMBER}").push()
                    }
                }
            }
        }
        // Stage 7: Deploy
        stage('Deploy with Docker Compose') {
    steps {
        script {
            dockerCompose.useComposeFiles = ['docker-compose.yml']
            dockerCompose.up('--build', '-d')
            }
        }
    }
        
    // Notifications & Cleanup
    post {
        always {
            emailext (
                to: 'lazzezmed@gmail.com',
                subject: 'Résultat du Pipeline kaddem-DevOps-Pipeline',
                body: """
                    <p>Statut du pipeline <b>kaddem-DevOps-Pipeline</b> (Build #${env.BUILD_NUMBER}) : 
                    <span style="color:${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'}">${currentBuild.currentResult}</span></p>
                    <p><b>Durée :</b> ${currentBuild.durationString}</p>
                    <p><b>Logs :</b> <a href="${env.BUILD_URL}console">Console Jenkins</a></p>
                """,
                attachLog: (currentBuild.currentResult != 'SUCCESS')
            )
        }
        cleanup {
            sh 'docker-compose down || true'
        }
    }
}
