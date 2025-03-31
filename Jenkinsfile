pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

     environment {
            DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
            BACKEND_IMAGE = "hamzasayari-4twin2-g1-kaddem-api"
            BACKEND_TAG = "${BUILD_NUMBER}"
            NEXUS_REPO = "http://192.168.33.10:8083/repository/maven-snapshots/"
        }

    stages {
            stage('Checkout Backend Code') {
                steps {
                    dir('backend') {
                        git branch: 'AyariHamza-4TWIN2-G1',
                            url: 'https://github.com/hamzaayarii/4TWIN2-G1-kaddem-project.git'
                    }
                }
            }

        stage('Maven Clean Compile') {
                    steps {
                    dir('backend') {
                        sh 'mvn clean'
                        echo 'Running Maven Compile'
                        sh 'mvn compile'
                    }
                    }
                }

                stage('Tests - JUnit/Mockito') {
                    steps {
                    dir('backend') {
                        sh 'mvn test'
                    }
                    }
                }

        stage('Backend - SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'scanner'
                    withSonarQubeEnv('scanner') {
                        dir('backend') {
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
        }

        stage('Deploy JAR to Nexus') {
                    steps {
                        dir('backend') {
                            sh "mvn deploy -DaltDeploymentRepository=deploymentRepo::default::${NEXUS_REPO}"
                        }
                    }
                }

       stage('Build Docker Image') {
                   steps {
                       dir('backend') {
                           sh "docker build -t ${BACKEND_IMAGE}:${BACKEND_TAG} ."
                       }
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
    }

    post {
        always {
            script {
                currentBuild.result = currentBuild.currentResult
            }
        }
    }
}
