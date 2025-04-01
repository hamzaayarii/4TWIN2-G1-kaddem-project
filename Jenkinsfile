pipeline {
    agent any

    tools {
        maven 'M2_HOME'
    }

    environment {
        IMAGE_NAME = "alimaalej/kaddem-4twin2-g1-kaddem"
    }

    stages {
        stage('Build') {
             steps {
                sh 'mvn clean install'
             }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.token=sqa_7ca51cd60b771b362742bde590364eeeeaf9b621 -Dmaven.test.skip=true'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DaltDeploymentRepository=deploymentRepo::default::http://10.0.2.15:8081/repository/maven-snapshots/'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                // sh "docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest"
                // sh "docker push ${IMAGE_NAME}:latest"
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerr') {
                        docker.image("${IMAGE_NAME}:${BUILD_NUMBER}").push()
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                sh 'docker-compose up -d --build --force-recreate'
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker-compose down'
                sh 'docker system prune -f'
            }
        }
    }

    post {
            always {
                emailext (
                    to: 'mohamedali.maalej@esprit.tn',
                    subject: 'Résultat du Pipeline kaddem-DevOps-Pipeline',
                    body: """
                        <p>Statut du pipeline <b>kaddem-DevOps-Pipeline</b> (Build #${BUILD_NUMBER}) :
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
