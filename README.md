# 채팅 서버 구현
## WebSocket
양방향 실시간 통신 프로토콜.  
서버는 websocket endpoint를 만들고, client는 endpoint의 URI를 이용하여 서버와 연결하여,  
연결이 지속되고 있는 한 언제나 서로 메세지를 주고 받을 수 있다.  
초기 연결을 제외하면, 추가적인 HTTP 헤더가 없어 빠른 메시징이 가능하다.  
http와 같이 기본적으로 80포트를 사용하고, ssl을 적용하면 443포트를 사용한다.  

### 동작
Web Socket의 동작은 크게 핸드 쉐이킹(Opening Handshake), 데이터 전송(Data Transfer), 연결 종료(Close Handshake)의 과정으로 이루어진다.  
- 핸드 쉐이킹(Opening Handshake)  
- 데이터 전송(Data Transfer)  
- 연결 종료(Close Handshake)  

### 채팅 고도화
클라이언트들은 서버에 접속하면 개별 session을 가진다.  
이를 채팅방에 매핑시켜 보관하면 서버에 전달된 메시지를 특정방의 websocket 세션으로 보낼 수 있게 되어 개별 채팅방 구현이 가능하다.  

### STOMP
Simple Text Oriented Messaging Protocol(텍스트 기반 메시징 프로토콜)  
WebSocket을 통해 메시지를 교환하는 상위 레벨 프로토콜.  
특정 **주제(Topic)**에 대해 구독하고, 해당 주제에 메시지를 발행하는 방식으로 통신한다.(pub/sub)  
또한 메시지 헤더와 본문을 지원하여, 메시지의 다양한 메타데이터(예: 목적지, 인증 정보 등)를 전달할 수 있다.  
[WebSocket과 STOMP 이용하기](https://innu3368.tistory.com/213)  

## 보안 강화
### jwt Token
jwt Token을 메시지 헤더에 보내서 유효하지않은 token에 대해서는 처리하지않도록 해주었다.  
스프링 시큐리티와는 별도로 Handler 인터셉터를 설정해야한다.  

### ssl
메시지에 token을 보내는 방식은 서비스에 사용하기엔 보안적으로 취약하며, SSL을 이용하여 네트워크단에서 데이터가 암호화될 수 있도록 해야한다.  
ssl인증을 적용하면 ws -> wss로 변경되며 암호화가 이루어진다.  
~~~
ws://host:port/path?query
wss://host:port/path?query
~~~

## 메세지 큐
### redis
redis는 인메모리 DB로 메시지큐와 유사한 기능을 일부 제공한다.(Pub/Sub기능)  
따라서 주로 캐시, 세션 저장, 큐, 실시간 메시징에 자주 사용한다.  
메모리에 저장하기때문에 속도는 빠르나 영속적 저장을 위해서는 별도의 db를 사용하는 것이 좋다.  
[웹소켓으로 채팅서버 만들기](https://www.daddyprogrammer.org/post/4077/spring-websocket-chatting/)

### rabbitMQ
메시지 큐 시스템인 rabbitMQ를 사용하면 빠른 메시지전달과 비동기식 통신을 처리하는데 적합하다.  
메시지를 큐에 넣는순간부터 큐에서 처리가 완료될때까지 손실없이 처리할수있기때문에(재시도처리 가능)  
고도화 과정에서 redis에서 rabbitMQ로 메시지큐를 변경하였다.
(5672 기본 포트, 15672 웹 메니지먼트 포트, 61613 STOMP포트)  
`docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 -p 61613:61613 --restart=unless-stopped -e RABBITMQ_DEFAULT_USER=username -e RABBITMQ_DEFAULT_PASS=password rabbitmq:management`  
STOMP 호환 플러그인 설치  
`rabbitmq-plugins enable rabbitmq_stomp`  

#### RabbitMQ의 구동방식
1. 클라이언트(Producer)가 라우팅키가 포함된 메시지를 서버로 보내면 서버가 Rabbit Broker로 이를 위임한다.  
2. 이 메시지에는 어떤 Exchange와 Queue로 가야하는지에 대한 내용이 바인딩 되어있어서, 해당하는 Exchange로 메시지가 보내진 뒤,
3. Exchange에서는 메시지에 바인딩 되어있는 Queue로 메시지를 보낸다.  
4. 클라이언트(Consumer)는 Queue를 구독하여 주기적으로 polling하며 메시지(데이터)를 가져온다.  

[RabbitMQ를 사용해 메세지 주고 받기](https://velog.io/@power0080/Message-QueueRabbitMQ를-사용해-메세지-주고-받기)  
[Road To MQ WebSocket + STOMP + RabbitMQ로 메세징 (Docker)](https://velog.io/@joonoo3/Road-To-MQ-WebSocket-STOMP-RabbitMQ%EB%A1%9C-%EB%A9%94%EC%84%B8%EC%A7%95-Docker)  
[재능교환소 Spring Boot와 RabbitMQ로 확장 가능한 1:1 채팅 구축하기](https://velog.io/@10000ji_/재능교환소-Spring-Boot와-RabbitMQ로-확장-가능한-11-채팅-구축하기)  
[Project Spring + Stomp 테스트 하는 과정.. (실시간 채팅 구현)](https://woo0doo.tistory.com/38)  
구독 구현시 도움이 되었던 블로그 : [Spring Boot WebSocket과 채팅 (4) - RabbitMQ](https://dev-gorany.tistory.com/325)  

## DB
영속적 데이터(채팅 메시지) 저장을 위해서 DB를 연동.  
### MySQL
기본 사용자 정보 및 채팅방 정보 등은 관계형database인 MySQL에서 관리하도록 한다.  
`docker run -p 3309:3306 -e MYSQL_ROOT_PASSWORD=1234 --name mysql-container mysql:lts`

#### CHAT_COOM
자동증가 ID를 사용할경우 roomId순서를 예측할수있기때문에 보안위험이 증가할수있다. 따라서 uuid를 사용하여 채팅방의 ID를 설정해주도록 하였다.  

#### CHAT_MEMBER
채팅방(CHAT_ROOM)과 회원(MEMBER)의 다대다 관계를 처리하기위해 생성한 테이블.  
복합키를 사용할경우 `@EmbeddedId`와 `@IdClass`중 어느것을 사용할것인지에 대한 고민.  
~~우선 엔티티 작성 비용을 줄이기 위하여 `@EmbeddedId`를 선택해서 구현하였다.~~  
복합키를 ID로만 다룰 수 있게 하여 DB 조회를 줄이고, 코드의 가독성도 개선할 수 있도록 @IdClass를 사용하기로 하였음  
[복합키를 가지는 JPA 엔티티 생성하기](https://woo-chang.tistory.com/43)  
[JPA 엔티티 복합키(Composite Primary Keys) 매핑](https://ykh6242.tistory.com/entry/JPA-%EC%97%94%ED%8B%B0%ED%8B%B0-%EB%B3%B5%ED%95%A9%ED%82%A4Composite-Primary-Keys-%EB%A7%A4%ED%95%91)  
[복합키, 외래키 Entity 설정하기(@IdClass를 사용하여 식별관계 매핑)](https://ynzu-dev.tistory.com/entry/JPA-%EB%B3%B5%ED%95%A9%ED%82%A4-%EC%99%B8%EB%9E%98%ED%82%A4-Entity-%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0IdClass%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-%EC%8B%9D%EB%B3%84%EA%B4%80%EA%B3%84-%EB%A7%A4%ED%95%91)  

#### 비즈니스로직을 어디에 넣을것인가
[비즈니스로직을 엔티티에 넣기](https://velog.io/@kevin_/엔티티에-비즈니스-로직-찬반)  

### MongoDB
채팅 서버에서 필요한 빠른 쓰기 성능, 유연한 데이터 모델링, 실시간 데이터 처리, 수평적 확장성을 만족하는 database.  
MongoDB는 문서 지향 데이터베이스로, 정형화된 데이터 모델을 가지지 않고 각 채팅 메시지를 독립적인 문서로 저장할 수 있다.  
이는 채팅 메시지의 내용이나 포맷이 다양해지거나 변경되어도(이미지 등) 쉽게 데이터를 저장하고 관리할 수 있으며,  
여러 테이블 간에 조인 작업을 할 필요가 없어서 읽기/쓰기 처리가 빨라 대용량의 실시간 메시징 시스템에 적합하다.  
[MongoDB 설치법](https://www.mongodb.com/ko-kr/docs/manual/tutorial/install-mongodb-community-with-docker/)

#### 명령어 모음
- mongodb 접속  
`mongosh`  
- 데이터베이스 생성 및 접속  
`use [데이터베이스 이름]`  
- 계정생성  
`db.createUser({user: "testuser", pwd:"testuser", roles:["readWrite"]});`  
- 사용자 목록 확인  
`db.getUsers()`  
- 컬렉션에서 모든 데이터 탐색  
`db.[collection명].find()`


### JPA
#### 네이밍 규칙 설정
기본 테이블 생성 규칙을 대문자 + SNAKE CASE 로 만들기위해서  
UpperCaseSnakeCaseNamingStrategy.class를 생성하여 application파일에 추가하였다.  

#### querydsl + data jpa
[QueryDSL 설정과 Repository에서의 사용](https://sjh9708.tistory.com/174)  

#### querydsl pageable sort하는법
[QueryDSL Pageable 정렬하기](https://joanyi.tistory.com/83)  

#### querydsl DTO반환
[QueryDSL return Type에 대하여](https://velog.io/@kimhalin/QueryDSL-return-Type%EC%97%90-%EB%8C%80%ED%95%98%EC%97%AC)  

#### querydsl join
[QueryDsl Join + On절 + Fetch join](https://nomoreft.tistory.com/30#%EA%B8%B0%EB%B3%B8%EC%A0%81%EC%9D%B8-%EC%83%81%EA%B4%80%EA%B4%80%EA%B3%84-entity-join) 

#### 참고링크
[Querydsl으로 안전한 쿼리 작성하기 + DataJPA](https://devwithpug.github.io/java/querydsl-with-datajpa/#site-nav)


## 기본적인 처리
### 페이징
#### PageableeHandlerMethodArgumentResolver
아규먼트리졸버를 이용하여 offset과 limit값이 적절한 값이 들어오도록 처리하였음  

#### PageCustom
커스텀한 페이지네이션 값을 리턴해줄수있도록 하였음  

### 응답값 형식 지정하기
ApiResult를 이용하여 기본적인 응답값 형식을 지정하였음  



## TODO
SockJS -> STOMP로 변경  
refreshToken  
이전 메시지 내역 조회  
인원수 자동 새로고침  
메시지 읽었는지 여부  
마지막 메시지가 목록에 함께 뜨도록  
회원차단  
처음에 방을 만들었을때 바로 입장하게하고, 방에서 나가게되면(인원수가 0이면) 화면에 안보이게 했으면 좋겟음 -> 방 삭제처리  

## 참고
- 채팅서버 테스트 [websocket-debug-tool](https://jiangxy.github.io/websocket-debug-tool/)  