
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
3. dockerfile 생성
   ~~~
   FROM openjdk:8-jdk-alpine
   EXPOSE 8080
   ADD easy-notes-1.0.0.jar easy-notes-1.0.0.jar
   ENTRYPOINT ["java", "-jar", "easy-notes-1.0.0.jar"]
   ~~~
4. 