// Pipeline declarativo de CI/CD para user-crud.
// Etapas: Checkout -> Compile -> Unit Tests -> Dockerize -> Deploy (minikube).
// Reutiliza el Dockerfile multi-etapa y los manifests de k8s/ existentes.
pipeline {
    agent any

    options {
        timestamps()
        // Si una etapa falla, el pipeline se detiene y no continúa con las siguientes.
        skipStagesAfterUnstable()
    }

    environment {
        GIT_REPO   = 'https://github.com/ocardenasmartinez1984/kiro-sdd.git'
        GIT_BRANCH = 'main'
        IMAGE_NAME = 'user-crud'
        IMAGE_TAG  = 'latest'
        K8S_DEPLOYMENT = 'user-crud'
    }

    stages {
        stage('Checkout') {
            steps {
                // Limpia el workspace y fuerza el checkout del tip real de la rama,
                // evitando quedarse en una revisión cacheada/antigua.
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${GIT_BRANCH}"]],
                    userRemoteConfigs: [[url: "${GIT_REPO}"]],
                    extensions: [
                        [$class: 'WipeWorkspace'],
                        [$class: 'CloneOption', noTags: false, honorRefspec: true, shallow: false]
                    ]
                ])
                // Diagnóstico: mostrar la revisión efectivamente comprobada.
                sh 'git rev-parse HEAD && git log -1 --oneline'
            }
        }

        stage('Compile') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh './mvnw -B test'
            }
            post {
                always {
                    // Publica los informes de Surefire en la UI de Jenkins.
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Dockerize') {
            steps {
                // Construir contra el daemon Docker de minikube para que la imagen
                // quede disponible en el clúster sin publicarla en un registro.
                // El Deployment usa imagePullPolicy: IfNotPresent.
                sh '''
                    eval $(minikube docker-env)
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                '''
            }
        }

        stage('Deploy (minikube)') {
            steps {
                sh '''
                    kubectl apply -f k8s/
                    kubectl rollout status deployment/${K8S_DEPLOYMENT} --timeout=180s
                '''
            }
        }
    }

    post {
        success {
            echo "Pipeline completado: imagen ${IMAGE_NAME}:${IMAGE_TAG} desplegada en minikube."
        }
        failure {
            echo 'Pipeline fallido. Revisa los logs de la etapa que falló.'
        }
    }
}
