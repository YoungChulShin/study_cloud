
## 앱 배포 및 접속 테스트
### Hello app 샘플
1. 어플 배포
   - deployment 생성
      ~~~
      kubectl run hello-server --image gcr.io/google-samples/hello-app:1.0 --port 8080
      ~~~
2. 서비스 외부 노출
   - service 생성
      ~~~
      kubectl expose deployment hello-server --type LoadBalancer --port 80 --target-port 8080
      ~~~
3. 서비스 확장
   - deployment replicas 수 변경
      ~~~
      kubectl scale deployment hello-server --replicas=3
      ~~~
   - node/pod 정보 확인
      ~~~
      kubectl get node -o wide
      kubectl get pod -o wide
      ~~~
    
## 앱 배포 및 접속 테스트
1. google cloud sql에 mysql 생성
2. spring boot 애플리케이션 작성 및 jar 패키징
   - 테스트 코드 저장소: https://github.com/callicoder/spring-boot-mysql-rest-api-tutorial
3. dockerfile 생성
   ~~~
   FROM openjdk:8-jdk-alpine
   EXPOSE 8080
   ADD easy-notes-1.0.0.jar easy-notes-1.0.0.jar
   ENTRYPOINT ["java", "-jar", "easy-notes-1.0.0.jar"]
   ~~~
4. GCR(Google Container Registry)에 업로드
   ~~~
   gcloud builds submit --tag asia.gcr.io/{project id}/{저장 폴더} ./
   ~~~
   - 사전 인증: `gcloud auth configure-docker`
   - project id 확인: gcloud config list project
   - 참고 페이지: [Link](https://cloud.google.com/container-registry/docs/pushing-and-pulling?_ga=2.85999320.-1118888932.1577827910&_gac=1.262590846.1598043950.Cj0KCQjw4f35BRDBARIsAPePBHwsvv0ss-WXdKmGIK1ObtP1EthYns4xrA6TJdFFO-UN-bT-Ia7VqvgaArpPEALw_wcB)
5. 배포된 이미지를 바탕으로 kubernetes 실행
   ~~~
   kubectl run {deployment name} --image {image 위치} --port 8080
   ~~~
6. 외부 접속을 위한 Load Balancer 생성
   ~~~
   kubectl expose deployment {deployment name} --type LoadBalancer --port 80 --target-port 8080
   ~~~
7. Load Balancer의 external ip로 접속 테스트
   ~~~
   curl -v "http://{ip}:80/api/notes"
   ~~~

## configmap을 이용한 환경 정보 분리 테스트
1. k8s를 위한 별도의 환경 파일 생성
   - `application-k8s.properties`
   - 중요 값들을 환경 변수로 분리
   ~~~
   spring.datasource.url = jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/notes_app?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false
   spring.datasource.username = ${MYSQL_USER}
   spring.datasource.password = ${MYSQL_PASSWORD}
   ~~~
2. 신규로 빌드 패키지 파일 생성
3. docker file 생성
   - entrypoint에 active profile을 설정하는 코드를 넣어준다
   ~~~
   ENTRYPOINT ["java", "-Dspring.profiles.active=k8s",  "-jar", "{jar name}"]
   ~~~
4. 신규 이미지를 GCR에 등록한다
5. configmap 생성
   ~~~
   kubectl create configmap configmap-sample --from-literal=db.address="{ip}" --from-literal=db.port="{port}" --from-literal=db.user="{user}” --from-literal=db.password="{password}"
   ~~~
   - 느낌표가 있으면 `''`로 묶어준다
6. deployment를 생성하기 위한 yaml 파일 생성
   - env 값과 configmap 값을 매핑해준다
   ~~~yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
      name: bootsample
      labels:
         app: bootsample
   spec:
      replicas: 1
      selector:
         matchLabels:
               app: bootsample
      template:
         metadata:
               labels:
                  app: bootsample
         spec:
               containers:
               - name: bootsample
               image: {image location}
               env:
                  - name: MYSQL_HOST
                     valueFrom:
                     configMapKeyRef:
                           name: configmap-sample
                           key: db.address
                  - name: MYSQL_PORT
                     valueFrom:
                     configMapKeyRef:
                           name: configmap-sample
                           key: db.port
                  - name: MYSQL_USER
                     valueFrom:
                     configMapKeyRef:
                           name: configmap-sample
                           key: db.user
                  - name: MYSQL_PASSWORD
                     valueFrom:
                     configMapKeyRef:
                           name: configmap-sample
                           key: db.password
               ports:
               - containerPort: 8080 
   ~~~

## secret을 이용한 중요 정보 보호
1. secret 생성
   ~~~
    kubectl create secret generic securitytoken --from-literal user={value1} --from-literal password={value2}
   ~~~
2. 환경 설정 값에 security를 반영
   ~~~yaml
   env:
      - name: MYSQL_HOST
      valueFrom:
         configMapKeyRef:
            name: configmap-sample
            key: db.address
      - name: MYSQL_PORT
      valueFrom:
         configMapKeyRef:
            name: configmap-sample
            key: db.port
      - name: MYSQL_USER
      valueFrom:
         secretKeyRef:
            name: securitytoken
            key: user
      - name: MYSQL_PASSWORD
      valueFrom:
         secretKeyRef:
            name: securitytoken
            key: password
   ~~~
