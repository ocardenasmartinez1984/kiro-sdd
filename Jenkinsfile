// Pipeline declarativo de CI/CD para user-crud.
// Etapas: Checkout -> Build & Test -> Dockerize -> Deploy (minikube).
// El JAR se construye una sola vez aquí y el Dockerfile (runtime-only) lo reutiliza.
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
        // Repositorio local de Maven persistente entre builds (evita re-descargar
        // dependencias). jenkins_home suele ser un volumen persistente.
        MAVEN_LOCAL_REPO = '/var/jenkins_home/.m2/repository'
        // Flags de rendimiento: sin transfer progress (-ntp) y build paralelo (-T 1C).
        MVN_ARGS = "-B -ntp -T 1C -Dmaven.repo.local=${MAVEN_LOCAL_REPO}"
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

        stage('Build & Test') {
            steps {
                // Un solo ciclo Maven compila, prueba y empaqueta el JAR ejecutable.
                sh 'chmod +x mvnw'
                sh './mvnw ${MVN_ARGS} clean package'
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
                // El Dockerfile es runtime-only: solo copia el JAR ya construido arriba.
                // Se construye contra el daemon Docker de minikube para que la imagen
                // quede disponible en el clúster (Deployment usa imagePullPolicy: IfNotPresent).
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
