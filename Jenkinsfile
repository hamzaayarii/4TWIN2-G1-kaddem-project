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
                def buildStatus = currentBuild.currentResult
                def buildUser = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')[0]?.userId ?: 'Github User'

                emailext(
                    subject: "Pipeline ${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                    <p>This is a Jenkins BINGO CICD pipeline status.</p>
                    <p>Project: ${env.JOB_NAME}</p>
                    <p>Build Number: ${env.BUILD_NUMBER}</p>
                    <p>Build Status: ${buildStatus}</p>
                    <p>Started by: ${buildUser}</p>
                    <p>Build URL: <a href='${env.BUILD_URL}'>${env.BUILD_URL}</a></p>
                    """,
                    to: 'hamzosayari07@gmail.com',
                    from: 'hamzosayari07@gmail.com',
                    replyTo: 'hamzosayari07@gmail.com',
                    mimeType: 'text/html',
                    attachmentsPattern: 'trivyfs.txt, trivyimage.txt'
                )
            }
        }
    }
}