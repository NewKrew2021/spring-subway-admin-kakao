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

# 지하철 노선도 미션

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

## ✏️ Code Review Process

[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/spring-subway-admin-kakao/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/spring-subway-admin-kakao/blob/master/LICENSE) licensed.

## 기능 요구사항

### Line

- 노선을 생성한다.
  - Line 생성
    - 중복되는 이름이 존재하면 400 코드 반환
  - Section 생성
    - 입력받은 upStationId, downStationId, distance로 Section 생성
    - stationID가 존재하지 않으면 400 코드 반환
  ##### Request
  ```
  POST /lines HTTP/1.1 accept: */*
  content-type: application/json; charset=UTF-8
  
  {
  "color": "bg-red-600",
  "name": "신분당선"
  }
  ```
  ##### Response
  ```
  HTTP/1.1 201 
  Location: /lines/1
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  {
  "id": 1,
  "name": "신분당선",
  "color": "bg-red-600"
  }
  ```
  
- 전체 노선 목록을 조회한다.
  ##### Request
  ```
  GET /lines HTTP/1.1
  accept: application/json
  host: localhost:49468
  ```
  ##### Response
  ```
  HTTP/1.1 200
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  [
  {
  "id": 1,
  "name": "신분당선",
  "color": "bg-red-600"
  },
  {
  "id": 2,
  "name": "2호선",
  "color": "bg-green-600"
  }
  ]
  ```
  
- 노선을 조회한다.
  - 노선의 상행 종점부터 하행 종점까지의 역 목록을 함께 응답
  - 노선 id가 존재하지 않으면 400 코드 반환
  ##### Request
  ```
  GET /lines/1 HTTP/1.1
  accept: application/json
  host: localhost:49468
  ```
  ##### Response
  ```
  HTTP/1.1 200
  Content-Type: application/json
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  
  {
  "id": 1,
  "name": "신분당선",
  "color": "bg-red-600"
  }
  ```
    
- 노선을 수정한다.
  - 노선의 이름, 색상을 수정한다.
  - 노선 id가 존재하지 않으면 400 코드 반환
  ##### Request
  ```
  PUT /lines/1 HTTP/1.1
  accept: */*
  content-type: application/json; charset=UTF-8
  content-length: 45
  host: localhost:49468
  
  {
  "color": "bg-blue-600",
  "name": "구분당선"
  }
  ```
  ##### Response
  ```
  HTTP/1.1 200
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```
    
- 노선을 삭제한다.
  - 노선 id가 존재하지 않으면 400 코드 반환
  ##### Request
  ```
  DELETE /lines/1 HTTP/1.1
  accept: */*
  host: localhost:49468
  ```
  ##### Response
  ```
  HTTP/1.1 204
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```

### Section

- 구간을 추가한다.
  - 정상적으로 추가되었으면 200 반환
  - stationId가 유효하지 않으면 400 반환
  - 구간을 추가할 수 없으면 500 반환
    - 상행역, 하행역이 모두 노선에 등록되어 있는 경우
    - 상행역, 하행역이 모두 노선에 등록되어 있지 않은 경우
    - 기존 구간보다 distance가 크거나 같은 경우
  ##### Request
  ```
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
  ##### Response
  ```
  HTTP/1.1 204
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```

- 구간을 제거한다.
  - stationId가 포함된 구간을 제거한다.
  - stationId가 유효하지 않은 경우 400 반환
  - 제거할 수 없는 경우(노선의 마지막 구간일 때) 500 반환
  ##### Request
  ```
  DELETE /lines/1/sections?stationId=2 HTTP/1.1
  accept: */*
  host: localhost:52165
  ```
  ##### Response
  ```
  HTTP/1.1 204
  Date: Fri, 13 Nov 2020 00:11:51 GMT
  ```
