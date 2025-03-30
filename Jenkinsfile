pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
        nodejs 'NodeJS_22'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials') // Create this in Jenkins

        BACKEND_IMAGE_NAME = "hamzabox/kaddem-devops"
        BACKEND_IMAGE_TAG = "${BUILD_NUMBER}"

        FRONTEND_IMAGE_NAME = "hamzabox/kaddem-frontend"
        FRONTEND_IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Repositories') {
            steps {
                script {
                    sh 'mkdir -p backend frontend'

                    dir('backend') {
                        git branch: 'AyariHamza-4TWIN2-G1',
                            url: 'https://github.com/hamzaayarii/4TWIN2-G1-kaddem-project.git'
                    }

                    dir('frontend') {
                        git branch: 'pre-prod',
                            url: 'https://github.com/hamzaayarii/devops-kaddem-frontend.git'
                    }
                }
            }
        }

        /*
        All other stages are commented out
        */
    }

         post {
               always {
                   emailext (
                       to: 'hamzosayari07@gmail.com',
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