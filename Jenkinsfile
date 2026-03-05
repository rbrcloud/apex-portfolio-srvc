@Library("apex-shared-library") _
apexPipeline {
    serviceName = "apex-portfolio-srvc"
}

// pipeline {
//     agent any
//
//     tools {
//         jdk 'sdkman-java'
//     }
//
//     environment {
//         SPRING_PROFILES_ACTIVE = "test"
//     }
//
//     stages {
//         stage("Checkout") {
//             steps {
//                 checkout scm
//             }
//         }
//
//         stage("Compile") {
//             steps {
//                 sh "./mvnw clean compile"
//             }
//         }
//
//         stage("Unit tests") {
//             steps {
//                 sh "./mvnw test"
//             }
//         }
//     }
//
//     post {
//         always {
//             junit 'target/surefire-reports/*.xml'
//         }
//     }
// }