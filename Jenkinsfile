pipeline {
    agent {
            docker {
                image 'node:20' // Khai báo thẳng môi trường bạn muốn
                // args '-u root' // Bỏ comment dòng này nếu bước npm install bị lỗi permission denied
            }
        }

    environment {
        CI = 'true'
    }

    stages {
        stage('Install Dependencies') {
            steps {
                echo 'Installing dependencies inside Node 20 container...'
                // Không cần quan tâm thư mục nữa, Jenkins tự map code vào thẳng container
                sh 'npm install'
            }
        }

        stage('Test Phase') {
            steps {
                echo 'Running unit tests...'
                sh 'npm run test -- --ci --coverage --testResultsProcessor="jest-junit"'
            }
        }
        
        stage('Build Phase') {
            steps {
                echo 'Building the application...'
                sh 'npm run build'
            }
        }
    }

    post {
        always {
            echo 'Uploading Test Results and Coverage...'
            
            // 1. Upload Test Result (Yêu cầu file định dạng chuẩn JUnit XML)
            // Cần cài JUnit Plugin trên Jenkins
            junit 'junit.xml' // Thay đổi đường dẫn trỏ đúng vào file kết quả của bạn
            
            // 2. Upload Test Coverage
            // Cách đơn giản nhất là nén toàn bộ thư mục report HTML thành Artifacts
            archiveArtifacts artifacts: 'coverage/**', allowEmptyArchive: true
            
            // (Nâng cao) Nếu bạn có cài Cobertura Plugin trên Jenkins để xem UI biểu đồ coverage:
            // cobertura autoUpdateHealth: false, autoUpdateStability: false, coberturaReportFile: 'coverage/cobertura-coverage.xml', conditionalCoverageTargets: '70, 0, 0', failUnhealthy: false, failUnstable: false, lineCoverageTargets: '80, 0, 0', maxNumberOfBuilds: 0, methodCoverageTargets: '80, 0, 0', onlyStable: false, sourceEncoding: 'ASCII', zoomCoverageChart: false
        }
        success {
            echo 'Pipeline passed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}