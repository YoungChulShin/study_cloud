
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