pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3'
    }
    environment {
        DOCKER_IMAGE = "lazztn/lazzezmohamedamine-4twin2-g1-kaddem"
    }
    stages {
        // Stage 1: Build with Maven
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        // Stage 2: Run JUnit tests
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

        // Stage 3: SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=4TWIN2-G1-kaddem-project'
                }
            }
        }

        // Stage 4: Deploy to Nexus
        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }

        // Stage 5: Docker Build & Push
        stage('Docker Build & Push') {
            steps {
                script {
                    docker.withRegistry('https://hub.docker.com', 'dockerr') {
                        def image = docker.build("${DOCKER_IMAGE}:${env.BUILD_NUMBER}")
                        image.push()
                    }
                }
            }
        }
    }
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
                    <p><b>Docker Image:</b> ${DOCKER_IMAGE}:${env.BUILD_NUMBER}</p>
                """,
                attachLog: (currentBuild.currentResult != 'SUCCESS')
            )
        }
    }
}
