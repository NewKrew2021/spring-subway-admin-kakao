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



---
구현해야 할 기능 목록

1단계 - 역/노선 관리 기능
* 지하철 역과 노선 이름은 중복이 될 수 없음.
* 지하철 역 관리 API 기능 완성하기
* 지하철 노선 관리 API 구현하기
    - 노선 생성
    - 노선 목록 조회
    - 노선 조회
    - 노선 수정
    - 노선 삭제

2단계 - 구간 관리 기능
* 지하철 노선 추가 API 수정
    - 두 종점간의 연결 정보이용하여 노선 추가 시 구간(Section) 정보도 함께 등록
* 지하철 구간 추가 API 구현
    - 상행 종점 등록
    - 하행 종점 등록
    - 갈래길 방지
    - 예외 처리
        - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
        - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
        - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
* 노선 조회 시 구간에 포함된 역 목록 응답
* 지하철 구간 제거 API
