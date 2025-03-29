pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3'
    }
    stages {
        // Étape 1 : Build avec Maven
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        // Étape 2 : Exécution des tests JUnit
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
        // Étape 3 : Analyse SonarQube
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
    sh 'mvn sonar:sonar -Dsonar.projectKey=4TWIN2-G1-kaddem-project'
                }
            }
        }
        // Étape 4 : Déploiement vers Nexus
        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests'
            }
        }
    }
    post {
    always {
        emailext (
            to: 'lazzezmed@gmail.com',
            subject: 'Résultat du Pipeline kaddem-DevOps-Pipeline',
            body: """
                <p>Statut du pipeline <b>kaddem-DevOps-Pipeline</b> (Build #${env.BUILD_NUMBER}) : <span style="color:${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'}">${currentBuild.currentResult}</span></p>
                <p><b>Durée :</b> ${currentBuild.durationString}</p>
                <p><b>Logs :</b> <a href="${env.BUILD_URL}console">Console Jenkins</a></p>
            """,
            attachLog: (currentBuild.currentResult != 'SUCCESS')
        )
    }
}
}
