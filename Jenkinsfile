
pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
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


         stage('docker image Stage') {
              steps {
                      // Build from local directory
                      sh 'docker build -t kaddem:1.0.0 .'
                      // Tag for Docker Hub
                      sh 'docker tag kaddem:1.0.0 hamzabox/kaddem-devops:1.0.0'
                      // Push to Docker Hub (requires Docker Hub credentials)
                      sh 'docker push hamzabox/kaddem-devops:1.0.0'
                  }
     }



}
}