pipeline {
    agent any // Chạy trên bất kỳ node/agent nào đang rảnh

    tools {
        nodejs 'Node01' // Đảm bảo bạn đã cấu hình NodeJS tool trong Jenkins với tên 'Node01'
    }
    // Định nghĩa các biến môi trường nếu cần
    environment {
        CI = 'true'
    }

    stages {
        stage('Checkout') {
            steps {
                // Tự động kéo code từ branch đang được trigger
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                echo 'Installing dependencies...'
                // Ví dụ với npm. Nếu dùng yarn thì thay bằng yarn install
                sh 'npm install'
            }
        }

        stage('Test Phase') {
            steps {
                echo 'Running unit tests and generating coverage...'
                // Chạy test và yêu cầu xuất coverage
                // Lưu ý: Package.json của bạn cần cấu hình thư viện test (như Jest) 
                // để xuất ra file test-results.xml và thư mục coverage/
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