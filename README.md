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


## 기능 요구 사항
- 지하철역 관리 API 기능
  1) 지하철 데이터 가져올 수 있게 수정
  2) stationDao 데이터 공통으로 사용할 수 있게끔 수정
  3) 지하철 역 중복 안되게 수정
  4) 노선/구간에서 지하철역 사용중이지 않은 경우 삭제
- 지하철 노선 관리 API 기능
  1) 노선 생성 -> 첫 노선 생성시 구간도 함께 생성
  2) 전체 노선 목록 조회
  3) 해당 노선 id에 해당하는 노선 조회
  4) 노선 정보 수정
  5) 구간이 1개인 경우에만 노석 삭제 가능하게끔 구현
- 지하철 구간 관리 API 기능
  1) 노선에 구간 추가
     1) 추가하려는 구간에 있는 두 역이 노선에 포함되어 있지 않은 경우 예외 처리
     2) 추가하려는 구간의 길이가 더 긴 경우 예외 처리
     3) 중복되는 구간 추가하려는 경우 예외 처리
  2) 구간 삭제
     1) 구간이 1개밖에 없는 경우 예외 처리
     2) 종점을 포함하는 구간을 삭제하는 경우
     3) 중간에 있는 구간을 삭제하는 경우
  

