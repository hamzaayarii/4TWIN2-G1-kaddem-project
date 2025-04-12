pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

     environment {
            DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
            DOCKER_USERNAME = "hamzabox"
            BACKEND_IMAGE = "${DOCKER_USERNAME}/ayarihamza-g1-kaddem-api"
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
         stage('Status Mysql') {
                    steps {
                         script {
                                    sh '''
                                        for i in {1..10}; do
                                          docker exec mysql-kaddem mysqladmin ping -h "localhost" -u"kaddemuser" -p"kaddempassword" && break
                                          echo "Waiting for MySQL..."
                                          sleep 5
                                        done
                                    '''
                                }
                    }
                }
         stage('Maven Clean Compile') {
                    steps {
                        sh 'mvn clean'
                        echo 'Running Maven Compile'
                        sh 'mvn compile'
                    }
                }
                stage('Tests - JUnit/Mockito') {
                    steps {
                        sh 'mvn test'
                    }
                }
                stage('Build package') {
                    steps {
                        sh 'mvn package'
                    }
                }
                stage('Maven Install') {
                    steps {
                        sh 'mvn install'
                    }
                }

          stage("SonarQube Analysis") {
                    steps {
                        withSonarQubeEnv('scanner') {

                            sh 'mvn sonar:sonar'

                       }
                    }
                }
/*
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
        */
    }

    post {
        always {
            script {
                currentBuild.result = currentBuild.currentResult
            }
        }
    }
}
