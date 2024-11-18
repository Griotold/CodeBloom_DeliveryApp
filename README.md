<div align="center">

  # 🌸Team CodeBloom🌸
  
  ### ✨음식 주문 관리 플랫폼 프로젝트✨
</div>

## 🗨️ About Project
가게 주인은 관리자에게 가게 등록을 요청하고, 일반 사용자는 등록된 가게의 메뉴를 주문할 수 있는 형태의
음식 주문 관리 플랫폼의 RestfulAPI 를 개발하였습니다. GPT의 API를 통해 가게 사장님이 상품 설명을 쉽게 작성할 수 있도록 지원합니다.

### 프로젝트 기간
- `2024.11.06 ~ 2024.11.18`

### 목적
- **백엔드 프로젝트** : 기획자, 웹디자이너,프론트 엔지니어의 기능/비기능 요구사항을 구체화합니다.
- **팀** **프로젝트** : 백엔드개발 팀의 일원으로 팀원과 협업을 통해 통합된 어플리케이션을 개발해 봅니다.
- **AI서비스** : 생성형 인공지능 서비스(API)와 연동하여 어플리케이션에 AI기능을 개발해 봅니다.



## 🤝 Member
| 문시원 | 박은우 | 조해성 |
|:----:|:------:|:------:|
|<img width=150 src="https://avatars.githubusercontent.com/u/105481797?v=4" />|<img width=150 src="https://avatars.githubusercontent.com/u/101847661?v=4" />|<img width=150 src="https://avatars.githubusercontent.com/u/101307758?v=4" />|
| `인증인가` `유저` `배송지` `가게요청` `카테고리` | `AI` `메뉴` `결제` `리뷰` `배포` | `가게` `주문` `결제` |

## 🛠️ Skills
- Java 17
- Gradle
- Spring Boot 3.3.5
- Spring Data JPA
- PostgreSQL
- GitHub
- AWS EC2
- AWS RDS

## 🖼 ERD
![image](https://github.com/user-attachments/assets/0142f7b6-b47b-4dbc-bc60-5ccb139473d4)

## ⚙️ Infra
![image](https://github.com/user-attachments/assets/bbbdcad0-b90a-4a65-8271-c5002611d1a0)

## 📃 API  Docs

[⛓ API 명세서 링크 ](https://www.notion.so/teamsparta/208c6c8959ca45b394152c0020429ad1?v=0892402299ee444db27d63b0c7c47d36&pvs=4)



## 🗂 Folder Architecture

```java
Project
├── java
│   └── com
│       └── sparta
│           └── project
│               ├── ProjectApplication.java
│               ├── config
│               │   ├── ....
│               │   └── jwt
│               │       ├── ....
│               ├── controller
│               │   ├── ....
│               ├── domain
│               │   ├── ....
│               ├── dto
│               │   ├── ....
|               ├── exception
│               │   ├── ....
|               ├── permission
│               │   ├── ....
│               ├── repository
│               │   ├── ....
|               ├── scheduler
│               │   ├── ....
│               └── service
│                   ├── ....
└── resources
    └── application.yml
 ....
```

## 🎠 Run
### 1. 프로젝트 클론
```Bash
git clone https://github.com/ewoo14/CodeBloom.git
cd CodeBloom
```
### 2. 애플리케이션 실행
- **Gradle 명령어로 실행**
```Bash
./gradlew bootRun
```
- **JAR 파일로 실행**
```Bash
./gradlew bootJar
java -jar build/libs/CodeBloom-0.0.1-SNAPSHOT.jar
```

### 3. 애플리케이션 접속
```
http://localhost:8080
```







