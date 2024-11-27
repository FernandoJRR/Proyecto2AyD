pipeline {
    agent any

    stages{
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/FernandoJRR/Proyecto2AyD.git'
            }
        }
        stage('Run Unit Tests') {
            steps {
                dir('api_reservaciones') {
                    echo 'Running unit tests...'
                    sh 'java --version'
                    sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
                    export PATH=$JAVA_HOME/bin:$PATH
                    java --version
                    /opt/homebrew/bin/mvn test -Dmaven.test.failure.ignore=true -Dspring.profiles.active=local
                    '''
                }
            }
            post {
                always {
                    junit skipPublishingChecks: true, testResults: 'api_reservaciones/target/surefire-reports/*.xml'
                }
                failure {
                    echo "Some tests failed. Check the report for details."
                }
            }
        }
        stage('Build Backend') {
            steps {
                echo 'Spring Boot Application build...'
                // Aquí se comenta la compilación real del backend
                dir('api_reservaciones') {
                    echo 'Building Spring Boot Application...'
                    sh '/opt/homebrew/bin/mvn clean'
                    sh '/opt/homebrew/bin/mvn clean package -DskipTests -Dspring.profiles.active=local'
                    sh '/usr/local/bin/docker-compose down'
                    dir('target') {
                        sh 'mv api_reservaciones-0.0.1-SNAPSHOT.war ROOT.war'
                    }
                    sh 'mv target/ROOT.war deploy'
                    sh '/usr/local/bin/docker-compose up -d'
                }
            }
        }
        stage('Build Frontend') {
            steps {
                echo 'Skipping frontend build step for now...'
                // Comentar o agregar pasos aquí
                dir('app_reservaciones') {
                    echo 'Building Angular Application...'
                    sh '''
                        export NVM_DIR="$HOME/.nvm"
                        [ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"
                        [ -s "$NVM_DIR/bash_completion" ] && . "$NVM_DIR/bash_completion"
                        nvm use 22  # Replace '16' with the Node.js version you want
                        npm install
                    '''
                    sh '''
                        export NVM_DIR="$HOME/.nvm"
                        [ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"
                        [ -s "$NVM_DIR/bash_completion" ] && . "$NVM_DIR/bash_completion"
                        nvm use 22  # Replace '16' with the Node.js version you want
                        npm run build --prod
                    '''
                }
            }
        }
        stage('Restart Services') {
            steps {
                echo 'Skipping Nginx restart for now...'
                sh '/opt/homebrew/bin/brew services restart nginx'
            }
        }
    }

    post {
        success {
            echo 'Build y despliegue completado exitosamente.'
        }
        failure {
            echo 'La build o el despliegue fallaron.'
        }
    }
}
