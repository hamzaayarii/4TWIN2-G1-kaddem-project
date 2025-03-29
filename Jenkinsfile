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
        failure {
            mail to: 'sheeshkabeb1@gmail.com', subject: 'Échec du Pipeline', body: 'Le pipeline ${env.JOB_NAME} a échoué.'
        }
    }
}
