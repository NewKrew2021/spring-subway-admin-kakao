# 지하철 노선도 미션

카카오 신입사원 교육 - 스프링 과정 실습을 위한 지하철 노선도 애플리케이션


역(Station): 아이디 + 이름으로 구성된 역.  
구간(Section): 역과 역 사이. 개념적으로 노선에 포함됨. 거리 정보 포함.  
노선(Line): 종점과 종점 사이, 개념적으로 구간을 1개 이상 가짐. 이름, 색깔, 거리 정보 포함.  
* 노선에는 갈래길이 없다. 사이클이 없는 더블 링크드 리스트와 같다.  

## 요구사항 정리

1. 지하철 역(Station) 관리 API 가능 완성하기
- 테스트 케이스 통과하도록 작성
- StationDao 접근 시, 매번 새로운 객체 생성하지 않고 기존 객체 사용하도록 제작

2. 지하철 노선(Line) 도출하기
- 도메인 객체, 컨트롤러, Dao 구현

3. 지하철 노선(Line) 관리 API 구현하기
- 테스트 케이스 통과하도록 작성
- 등록한 정보와 등록된 정보가 일치하는지 테스트 추가
- 데이터베이스 없이 ID를 생성해서 자료구조에 저장

4. 지하철 구간(Section) 도출하기
- 도메인 객체, 컨트롤러, Dao, 응답 객체 구현하기

5. 지하철 구간(Section) 관리 API 구현하기 (추가, 조회, 삭제)
- 테스트 케이스 통과하도록 작성
- 구간 변경시, 영향을 받은 구간들의 거리가 예상과 맞는지 테스트
- 노선은 여러 구간을 포함하므로, 수정과 삭제에 대해 관련된 처리 수행
- 노선 추가 시, 구간 정보도 추가
- 데이터베이스 없이 ID를 생성해서 자료구조에 저장

6. 프론트엔드 코드에서 잘 동작하도록 하기
- 빈 필수 입력값에 대한 예외처리 (없음, 공백)
- 잘못된 입력값에 대한 예외처리 (이름/아이디 중복, 형식/길이 다름)

7. 스프링 프레임워크 적용하기
- 스프링 Bean, 스프링 JDBC 적용
- schema.sql 작성
- H2 DB를 통해 저장된 값 확인
* https://edu.nextstep.camp/s/bb4PXLji/ls/EFFe0uW3 힌트 참고

## 🚀 Getting Started

### Install

#### npm 설치

----
cd frontend
npm install
----

____

`frontend` 디렉토리에서 수행해야 합니다.

____

### Usage

#### webpack server 구동

----
npm run dev
----

#### application 구동

----
./gradlew bootRun
----

## ✏️ Code Review Process

https://github.com/next-step/nextstep-docs/tree/master/codereview[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정]

## 🐞 Bug Report

버그를 발견한다면, https://github.com/next-step/spring-subway-admin-kakao/issues[Issues] 에 등록해주세요 :)

## 📝 License

This project is https://github.com/next-step/spring-subway-admin-kakao/blob/master/LICENSE[MIT] licensed.