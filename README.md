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

## 구현 기능

### 역 관리
- [x] 역 추가 (Dao 리스트에 역 추가)
- [x] 역 삭제 (Dao 리스트에 역 삭제)
- [x] 역 목록 조회
- [x] 역 컨트롤러 내 Dao 관리

### 노선 정보 관리
- [x] 노선 컨트롤러 개발
- [x] 노선 Dao 개발
- [x] 노선 객체 정의
- [x] 노선 생성 기능 구현
- [x] 노선 목록 조회 구현
- [x] 노선 조회 구현
- [x] 노선 수정 구현
- [x] 노선 삭제 구현
- [x] 지하철 노선 추가 API 수정 (상행, 하행, 거리도 함께 등록)

### 구간 관리
- [x] 구간 객체 정의
- [x] 구간 DAO 정의
- [x] 지하철역/노선/구간 객체 간 의존 관계 설정
- [x] 구간 생성 기능 (상행 종점 등록)
- [x] 구간 생성 기능 (하행 종점 등록)
- [x] 구간 생성 기능 (갈래길 방지)
- [x] 구간 제거 기능 (중간 역 제거 시 재배치)
- [x] 예외 처리 (역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록 불가)
- [x] 예외 처리 (상행역과 하행역이 이미 모두 노선에 등록되어 있다면 추가 불가)
- [x] 예외 처리 (상행역, 하행역 둘 중 하나라도 포함이 되지 않으면 추가 불가)
- [ ] 예외 처리 (구간이 하나인 노선에서는 종점을 제거 불가)

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/spring-subway-admin-kakao/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/spring-subway-admin-kakao/blob/master/LICENSE) licensed.
