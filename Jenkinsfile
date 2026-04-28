pipeline {
    // Gọi "anh thợ" Maven (đã cài sẵn Java JDK 17)
    agent {
        docker { 
            image 'maven:3.9.6-eclipse-temurin-17' 
            // Dòng args dưới đây giúp cache lại thư viện tải về để các lần build sau chạy nhanh như chớp
            args '-v $HOME/.m2:/root/.m2' 
        }
    }

    environment {
        CI = 'true'
    }

    stages {
        // ==========================================
        // CỤM 1: MEDIA SERVICE
        // ==========================================
        stage('Media Service') {
            // Yêu cầu 6: Chỉ chạy khi code trong thư mục media bị thay đổi
            when { 
                changeset "media/**" 
            }
            stages {
                stage('Build Media') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Media Service..."
                        // Không cần dùng dir() vì đã có Parent POM
                        sh 'mvn --projects media --also-make clean package -DskipTests'
                    }
                }
                
                stage('Test Media') {
                    steps {
                        echo "Đang chạy Test cho Media Service..."
                        sh 'mvn --projects media test'
                    }
                    post {
                        always {
                            echo "Đang lưu kết quả Test và Coverage của Media..."
                            // Đọc file kết quả test (có sẵn trên Jenkins)
                            junit 'media/target/surefire-reports/*.xml'
                            
                            // Cách 1: Nén HTML Report của Jacoco để xem trực tiếp (không cần cài thêm plugin)
                            archiveArtifacts artifacts: 'media/target/site/jacoco/**', allowEmptyArchive: true
                            
                            // Cách 2: Hoặc dùng plugin Jacoco (nếu máy Jenkins của bạn đã cài plugin Jacoco)
                            // jacoco execPattern: 'media/target/jacoco.exec'
                        }
                    }
                }
            }
        }

        // ==========================================
        // CỤM 2: PRODUCT SERVICE
        // ==========================================
        stage('Product Service') {
            when { 
                changeset "product/**" 
            }
            stages {
                stage('Build Product') {
                    steps {
                        echo "Phát hiện thay đổi. Đang build Product Service..."
                        sh 'mvn --projects product --also-make clean package -DskipTests'
                    }
                }
                stage('Test Product') {
                    steps {
                        echo "Đang chạy Test cho Product Service..."
                        sh 'mvn --projects product test'
                    }
                    post {
                        always {
                            junit 'product/target/surefire-reports/*.xml'
                            archiveArtifacts artifacts: 'product/target/site/jacoco/**', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
    }
}