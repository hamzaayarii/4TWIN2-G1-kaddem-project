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
               script {
                   currentBuild.result = currentBuild.currentResult
               }

               emailext subject: "Pipeline Status  ${currentBuild.result}: ${currentBuild.projectName}",
                    body: """<html>
                           <body>
                               <p>Dear Team,</p>
                               <p>The pipeline for project <strong>${currentBuild.projectName}</strong> has completed with the status: <strong>${currentBuild.result}</strong>.</p>
                               <p>Thank you,</p>
                               <p>Your Jenkins Server</p>
                           </body>
                       </html>""",
               to: 'hamzosayari07@gmail.com',
               mimeType: 'text/html'

             }
           }

       }