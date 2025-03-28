pipeline {
    agent any
    tools {
        maven 'Maven-3.9.3' // Assurez-vous que ce nom correspond à votre outil configuré dans Jenkins (Global Tool Configuration).
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
                    junit 'target/surefire-reports/*.xml' // Publie les résultats des tests dans Jenkins.
                }
            }
        }
        // Étape 3 : Analyse SonarQube
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube-Server') { // Configurez le serveur SonarQube dans Jenkins (Manage Jenkins > Configure System).
                    sh 'mvn sonar:sonar -Dsonar.projectKey=nomProjet'
                }
            }
        }
        // Étape 4 : Déploiement vers Nexus
        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy -DskipTests' // Assurez-vous que votre `pom.xml` contient la config Nexus (<distributionManagement>).
            }
        }
    }
    // Notifications (Bonus pour "Excellence")
    post {
        failure {
            mail to: 'email@example.com', subject: 'Échec du Pipeline', body: 'Le pipeline ${env.JOB_NAME} a échoué.'
        }
    }
}
