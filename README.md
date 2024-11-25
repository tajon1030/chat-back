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
[RabbitMQ를 사용해 메세지 주고 받기](https://velog.io/@power0080/Message-QueueRabbitMQ를-사용해-메세지-주고-받기)

## DB
영속적 데이터(채팅 메시지) 저장을 위해서 DB를 연동.  
### MySQL
기본 사용자 정보 및 채팅방 정보 등은 관계형database인 MySQL에서 관리하도록 한다.

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