
pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    stages {
        stage('GIT') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/hamzaayarii/4TWIN2-G1-kaddem-project.git'
            }
        }

        stage('Compile Stage') {
            steps {
                sh 'mvn clean compile'
            }
        }
   stage('SonarQube Analysis') {
             steps{
                 script {
                     def scannerHome = tool 'scanner'
                    withSonarQubeEnv {
                         sh "${scannerHome}/bin/sonar-scanner"
                                      }
                          }
                      }
   }

stage('Deploy to Nexus') {
    steps {
        script {
            sh """
            mvn deploy:deploy-file -Dfile=target/kaddem-0.0.1-SNAPSHOT.jar \
                -DrepositoryId=nexus-snapshots-repository \
                -Durl=http://192.168.33.10:8083/repository/maven-snapshots/ \
                -DgroupId=tn.esprit.spring \
                -DartifactId=kaddem \
                -Dversion=0.0.1-SNAPSHOT \
                -Dpackaging=jar
            """
        }
    }
}


         stage('docker image Stage') {
               steps {
              sh 'docker build -t kaddem:1.0.0 https://github.com/hamzaayarii/4TWIN2-G1-kaddem-project.git'
           }
     }



}
}