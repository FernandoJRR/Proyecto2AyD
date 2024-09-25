pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/FernandoJRR/Proyecto2AyD'
            }
        }
        stage('Build Backend') {
            steps {
                echo 'Simulating Spring Boot Application build...'
                // Aquí se comenta la compilación real del backend
                // dir('api_citas') {
                //     echo 'Building Spring Boot Application...'
                //     bat 'mvn clean package -DskipTests' // Compilar el proyecto y generar el archivo .war
                // }
            }
        }
        stage('Deploy Backend to Tomcat') {
            steps {
                echo 'Simulating Backend Deployment to Tomcat...'
                script {
                    //def warFile = 'api_citas/target/api_citas.war'
                    //def tomcatWebapps = 'C:\\tomcat\\webapps'
                    
                    // Simular reinicio de Tomcat
                    // bat 'C:\\tomcat\\bin\\shutdown.bat'
                    // bat 'C:\\tomcat\\bin\\startup.bat'
                    echo 'Tomcat restarted (simulation)'
                }
            }
        }
        stage('Build Frontend') {
            steps {
              echo 'Building Frontend Angular application'
                //dir('app_citas') {
                //    echo 'Building Angular Application...'
                //    bat 'npm install'
                //    bat 'npm run build --prod'
                //}
            }
        }
        stage('Restart Nginx') {
            steps {
                echo 'Restarting Nginx'
                //dir('C:\\nginx') {
                //    echo 'Restarting Nginx...'
                //    bat 'nginx -s reload'
                //}
            }
        }
    }
}
