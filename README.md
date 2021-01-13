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

<br>

## 요구사항 정리
* 1 단계 - 역 / 노선 관리기능
    1. 지하철 역 관리 API 기능 완성하기
        1. StationController를 통해 요청을 처리하는 부분은 미리 구현되어 있음
        2. StationDao를 활용하여 지하철 역 정보를 관리
    2. 지하철 노선 관리 API 구현하기
        1. 노선 생성 Request/Response
            * Request
            ```
            POST /lines HTTP/1.1
            accept: */*
            content-type: application/json; charset=UTF-8

            {
                "color": "bg-red-600",
                "name": "신분당선"
            }
            ```
            * Response
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
        2. 노선 목록 조회 Request/Response
            * Request
            ```
            GET /lines HTTP/1.1
            accept: application/json
            host: localhost:49468
            ```
            * Response
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
        3. 노선 조회 Request/Response
            * Request
            ```
            GET /lines/1 HTTP/1.1
            accept: application/json
            host: localhost:49468
            ```
            * Response
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
        4. 노선 수정 Request/Response
            * Request
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
            * Response
            ```
            HTTP/1.1 200
            Date: Fri, 13 Nov 2020 00:11:51 GMT
            ```
        5. 노선 삭제 Request/Response
            * Request
            ```
            DELETE /lines/1 HTTP/1.1
            accept: */*
            host: localhost:49468
            ```
            * Response
            ```
            HTTP/1.1 204
            Date: Fri, 13 Nov 2020 00:11:51 GMT
            ```
