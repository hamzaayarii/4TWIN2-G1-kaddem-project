pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_USERNAME = "hamzabox"
        BACKEND_IMAGE = "${DOCKER_USERNAME}/ayarihamza-4twin2-g1-kaddem-api"
        BACKEND_TAG = "${BUILD_NUMBER}"
        NEXUS_REPO = "http://192.168.33.10:8083/repository/maven-snapshots/"
    }

    stages {
        stage('Checkout Backend Code') {
            steps {
                git branch: 'AyariHamza-4TWIN2-G1',
                    url: 'https://github.com/hamzaayarii/4TWIN2-G1-kaddem-project.git'
            }
        }

        stage('Maven Build & Install') {
            steps {
                echo 'Running full Maven build lifecycle'
                sh 'mvn clean install'
            }
        }

        stage('Tests - JUnit/Mockito') {
            steps {
                sh 'mvn test'
            }
        }

        stage("SonarQube Analysis") {
            steps {
                withSonarQubeEnv('scanner') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy JAR to Nexus') {
            steps {
                sh "mvn deploy -DaltDeploymentRepository=deploymentRepo::default::${NEXUS_REPO}"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${BACKEND_IMAGE}:${BACKEND_TAG} ."
            }
        }

        stage('Push Docker Image to DockerHub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                        sh "docker push ${BACKEND_IMAGE}:${BACKEND_TAG}"
                    }
                }
            }
        }

        stage('Trigger Frontend Build') {
            steps {
                build job: 'kaddem-frontend', wait: true
            }
        }
    }

    post {
        success {
            mail(
                to: 'hamzosayari07@gmail.com',
                subject: "✅ SUCCESS: Pipeline ${currentBuild.fullDisplayName}",
                body: """Pipeline completed successfully 🎉

Job: ${env.JOB_NAME}
Build: ${env.BUILD_NUMBER}
Duration: ${currentBuild.durationString}

Backend App URL: ${APP_URL}
Jenkins URL: ${JENKINS_URL}
Build Details: ${env.BUILD_URL}
"""
            )
        }

        failure {
            mail(
                to: 'hamzosayari07@gmail.com',
                subject: "❌ FAILURE: Pipeline ${currentBuild.fullDisplayName}",
                body: """Pipeline failed 🚨

Job: ${env.JOB_NAME}
Build: ${env.BUILD_NUMBER}
Duration: ${currentBuild.durationString}

Check logs at: ${env.BUILD_URL}
"""
            )
        }

        always {
            script {
                currentBuild.result = currentBuild.currentResult
                echo "Pipeline result: ${currentBuild.result}"
            }
        }
    }
}
