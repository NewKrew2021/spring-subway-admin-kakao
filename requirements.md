# 기능 요구사항
## 1단계 - 역/ 노선 관리 기능 구현
### 1. 지하철 역 API 가능 완성하기
### 2. 지하철 노선 생성 기능
- request
```http request
POST /lines HTTP/1.1
accept: */*
content-type: application/json; charset=UTF-8

{
    "color": "bg-red-600",
    "name": "신분당선"
}
```
- response
```http response
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
### 3. 지하철 목록 조회
- request
```http request
GET /lines HTTP/1.1
accept: application/json
host: localhost:49468
```
- response
```http request
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
### 4. 노선 조회 
- request
```http request
GET /lines/1 HTTP/1.1
accept: application/json
host: localhost:49468
```
- response
```http request
HTTP/1.1 200 
Content-Type: application/json
Date: Fri, 13 Nov 2020 00:11:51 GMT

{
    "id": 1,
    "name": "신분당선",
    "color": "bg-red-600"
}
```
### 5. 노선 수정
- request
```http request
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
- response
```http request
HTTP/1.1 200 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```
### 6. 노선 삭제 
- request
```http request
DELETE /lines/1 HTTP/1.1
accept: */*
host: localhost:49468
```
- response
```http request
HTTP/1.1 204 
Date: Fri, 13 Nov 2020 00:11:51 GMT
```

## 2단계 - 구간 관리 기능
### 1. 지하철 노선 추가 API 수정
- 노선 추가시 구간(Section) 정보도 함께 등록
- request
```http request
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
### 2. 지하철 구간 추가 API 구현
- 노선에 구간을 추가하는 API 만들기
- request
```http request
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
### 3. 노선 조회 시 구간에 포함된 역 목록 응답
- response
```http request
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
### 4. 지하철 구간 제거 API
- request
```http request
DELETE /lines/1/sections?stationId=2 HTTP/1.1
accept: */*
host: localhost:52165
```
## 3단계 - 프레임워크 적용
### 1. 스프링 빈 활용하기
### 2. 스프링 JDBC 활용하기
### 3. H2 DB를 통해 저장된 값 확인하