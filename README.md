## 설명
- 해당 코드는 스프링에서 Amazon S3 CRUD 예제코드입니다. 이 예제코드에서는 다음과 같은 기능을 다룹니다.
  - S3 데이터 조회
  - S3 데이터 추가
  - S3 데이터 삭제
  - S3 데이터 다운로드

<br>

## 프로젝트 환경
- 프로젝트 환경구성은 다음과 같습니다.
  - JAVA 11
  - Spring Boot 2.6.1
  - Spring Cloud Aws 2.2.6
  - MySQL 8.0.27
  - Docker
    
<br>

## 테스트 하는법
- Amazon S3 Bucket 생성
  - S3 Console Management가 아닌 외부에서 S3에 데이터를 올릴려면 IAM 사용자를 추가하여 S3 관련 룰을 추가해줘야 합니다. <br>
  <a href='https://kim-jong-hyun.tistory.com/84' target='_blank'>S3 버킷 및 객체 생성</a> <br>
  <a href='https://kim-jong-hyun.tistory.com/85' target='_blank'>IAM으로 사용자 추가 및 S3 액세스 권한추가</a>
  
- Docker로 MySQL Container 실행
  - 프로젝트 루트경로로 접근하여 'docker-compose up' 명령어를 실행해줍니다. 해당 명령어는 Docker가 설치되어있어야 가능합니다.
  
- Spring Application에 bucket, access-key, secret-key 세팅
  - @SpringBootApplication 어노테이션이 선언된 JAVA 파일을 가보시면 별도 클래스를 만들어 두었습니다. 
    이 클래스는 Spring Boot가 구동되기전에 초기값을 설정하도록 구성하였으며 System.setProperty 속성값에 bucket, access-key, secret-key값을 셋팅해주시면 됩니다.
  - bucket, access-key, secret-key값을 세팅하시고 실행하시면 세팅한 값을 application.yml이 읽어드려 Amazon S3 설정파일을 구성합니다.
