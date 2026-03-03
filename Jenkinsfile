pipeline {
    agent any

    environment {
        SPRINT_PROFILES_ACTIVE = "test"
    }

    stages {
        stage("Checkout") {
            steps {
                checkout scm
            }
        }

        stage("Compile") {
            steps {
                sh "./mvnw clean compile"
            }
        }

        stage("Unit tests") {
            steps {
                sh "./mvnw test"
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}