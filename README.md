<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/next-step/spring-subway-admin-kakao">
</p>

<br>

# 지하철 노선도 미션​ :train2:
카카오 신입사원 교육 - 스프링 과정 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```
<br>



# 미션 설명

API 요청에 대한 처리와 Dao 객체를 활용하여 데이터를 관리하는 연습을 위한 미션

지하철 역과 지하철 노선을 관리하기 위한 API를 구현하기

미리 제공된 프론트엔드 코드에서 `역 관리` 기능과 `노선 관리`기능이 잘 동작하도록 완성하기 (노선 생성 시 **상행역**, **하행역**, **거리** 입력은 무시)



# 기능 구현 목록



## 1단계 기능목록 - 역/노선 관리 기능

> 지하철 역과 노선 이름은 중복이 될 수 없음



### 지하철 역 관리 API 기능 완성하기

- `StationController`를 통해 요청을 처리하는 부분은 미리 구현되어 있음
- `StationDao`를 활용하여 지하철 역 정보를 관리
  -[X] 역 생성 Request를 보냈을 때, dao에 지하철 이름 저장하기



### 지하철 노선 관리 API 구현하기

-[X] LineController 구현
  - 아래 API를 호출하는 메소드 구현



-[X] [POST] 노선 생성 Request / Response 
  - Request 를 받아서 그거에 해당하는 ID(서버가 가지고 있음)를 생성후 다시 Response로 돌려줌



-[X] [GET] 노선 목록 조회 Request / Response 
  - 노선에 등록된 구간 정보를 통해 노선목록을 응답하기



-[X] [GET] 노선 조회 Request / Response
  - 아이디 정보를 통한 id, name, color, 역 목록을 포함한 정보 호출



-[X] [PUT] 노선 수정 Request / Response
  - 아이디 정보를 통해서 color, name 를 Requset로 보내면 노선 수정
  - 성공 Response는 200



-[X] [DELETE] 노선 삭제 Request / Response
  - 아이디 정보를 통해서 Request를 보내면 그에 해당하는 노선 삭제
  - 성공 Response 는 204




## 2단계 기능 요구사항

### 지하철 노선 추가 API 수정 
- 노선 추가 시 3가지 정보를 추가로 입력 받음
  - upStationId: 상행 종점
  - downStationId: 하행 종점
  - distance: 두 종점간의 거리
- 두 종점간의 연결 정보이용하여 노선 추가 시 구간(Section) 정보도 함께 등록



- requset

```http
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선",
    "upStationId": "1",
    "downStationId": "2",
    "distance": "10"
}
```



### 지하철 구간 추가 API 구현

- 노선에 구간을 추가하는 API를 만들기
  - path는 /lines/{lineId}/sections 을 활용



- requset

```http
POST /lines/1/sections HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8
host: localhost:52165

{
    "downStationId": "4",
    "upStationId": "2",
    "distance": 10
}

```




### 노선 조회 시 구간에 포함된 역 목록 응답
- 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답



- response

```http
HTTP/1.1 201 
Location: /lines/1
Content-Type: application/json
Date: Fri, 13 Nov 2020 00:11:51 GMT

{
    "id": 1,
    "name": "신분당선",
    "color": "bg-red-600",
    "stations": [
        {
            "id": 1,
            "name": "강남역"
        },
        {
            "id": 2,
            "name": "역삼역"
        }
    ]
}
```



### 지하철 구간 제거 API

- request

```http
HTTP/1.1 201 
Location: /lines/1
Content-Type: application/json
Date: Fri, 13 Nov 2020 00:11:51 GMT

{
    "id": 1,
    "name": "신분당선",
    "color": "bg-red-600",
    "stations": [
        {
            "id": 1,
            "name": "강남역"
        },
        {
            "id": 2,
            "name": "역삼역"
        }
    ]
}
```



> 구간추가 제거시 [다음의 규칙](https://edu.nextstep.camp/s/bb4PXLji/ls/iKkQksyd) 을 따라야한다.


## 질문목록
- `private Line createNewObject(Line line)` 의 이유가 궁금하다.



## 📝 License

This project is [MIT](https://github.com/next-step/spring-subway-admin-kakao/blob/master/LICENSE) licensed.
