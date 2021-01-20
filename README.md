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

## 목차

1. 미션 소개
    1. [1단계](#1단계--역노선-관리-기능)
    2. [2단계](#2단계--구간-관리-기능)
    3. [3단계](#3단계--프레임워크-적용)
    4. [4단계](#4단계--리팩터링)
2. [실행 방법](#실행-방법)
3. [참고 링크](#참고-링크)
    1. 코드 리뷰 과정
    2. 버그 신고
    3. 라이선스

---

## 미션 소개

### 1단계 : 역/노선 관리 기능

미션 설명

* API 요청에 대한 처리와 Dao 객체를 활용하여 데이터를 관리하는 연습을 위한 미션
* 지하철 역과 지하철 노선을 관리하기 위한 API를 구현하기
* 미리 제공된 프론트엔드 코드에서 역 관리 기능과 노선 관리기능이 잘 동작하도록 완성하기  
(노선 생성 시 상행역, 하행역, 거리 입력은 무시)

프로그래밍 제약 사항

* __@Service, @Component 등 스프링 빈 활용 금지__
  * 스프링 컨테이너 사용 전/후의 차이를 명확히 경험하기 위해 스프링 빈 사용을 금지  
(API 요청을 받기위한 필수 컴포넌트를 제외 ex. @Controller)
  * 스프링 빈을 사용하지 않고 객체를 직접 생성하고 의존 관계를 맺어주기
* __데이터 저장은 XXXDao을 활용__
  * 1단계에서는 DB를 사용하지 않고 Dao객체 내부의 List에서 데이터를 관리함
  * 기능 구현간 필요한 로직은 추가가 가능하고 기존 코드도 변경이 가능함

```java
public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
```

기능 요구 사항

* 지하철 역과 노선 이름은 중복이 될 수 없음
* __지하철 역 관리 API 기능 완성하기__
  * StationController를 통해 요청을 처리하는 부분은 미리 구현되어 있음
  * StationDao를 활용하여 지하철역 정보를 관리
* __지하철 노선 관리 API 구현하기__

### 2단계 : 구간 관리 기능

미션 설명

* 지하철 역과 역 사이의 연결 정보인 지하철 구간을 도출하고 이를 관리하는 API를 만드는 미션
* 지하철역, 노선, 구간 객체의 의존 관계 설정을 연습해보는 미션
* 미리 제공된 프론트엔드 코드를 바탕으로 기능이 잘 동작하도록 완성하기
* 미리 제공된 프론트엔드 코드에서 노선 관리 기능과 구간 관리 기능이 잘 동작하도록 완성하기  
(노선 생성 시 상행역, 하행역, 거리 입력 포함하기, 없는 경우 예외처리)

프로그래밍 제약 사항

* __@Service, @Component 등 스프링 빈 활용 금지__
  * 스프링 컨테이너 사용 전/후의 차이를 명확히 경험하기 위해 스프링 빈 사용을 금지  
(API 요청을 받기위한 필수 컴포넌트를 제외 ex. @Controller)
  * 스프링 빈을 사용하지 않고 객체를 직접 생성하고 의존 관계를 맺어주기

기능 요구 사항

* __지하철 노선 추가 API 수정__
  * 노선 추가 시 3가지 정보를 추가로 입력 받음
    * upStationId: 상행 종점
    * downStationId: 하행 종점
    * distance: 두 종점간의 거리
  * 두 종점간의 연결 정보를 이용하여 노선 추가 시 __구간(Section) 정보도 함께 등록__

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

* __지하철 구간 추가 API 구현__
  * 노선에 구간을 추가하는 API를 만들기
  * path는 /lines/{lineId}/sections을 활용

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

* __노선 조회 시 구간에 포함된 역 목록 응답__
  * 노선에 포함된 구간 정보를 통해 상행 종점부터 하행 종점까지의 역 목록을 응답
  
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

* __지하철 구간 제거 API__

  ```http
  DELETE /lines/1/sections?stationId=2 HTTP/1.1
  accept: */*
  host: localhost:52165
  ```

기능 요구 사항 - 구간 등록/제거 부가 설명

* 새로 등록할 구간의 상행역과 하행역 중 노선에 __이미 등록되어있는 역을 기준으로__ 새로운 구간을 추가한다.
* 하나의 노선에는 __갈래길이 허용되지 않기 때문에__ 새로운 구간이 추가되기 전에 갈래길이 생기지 않도록 기존 구간을 변경한다.
* 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
* 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
* 중간역이 제거될 경우 재배치를 함
  * 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
  * 거리는 두 구간의 거리의 합으로 정함
* 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
* 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
* 구간이 하나인 노선에서 마지막 구간은 제거할 수 없음

### 3단계 : 프레임워크 적용

미션 설명

* 1,2단계에서 구현한 애플리케이션에 스프링 빈과 스프링 JDBC를 적용하기
* 직접 관리하던 객체들을 스프링 컨테이너가 관리할 수 있도록 설정하기
* XXXDao에서 관리하던 정보를 DB에서 관리할 수 있도록 변경하기
* 1단계에서 구현한 기능이 리팩터링 이후에도 여전히 정상 동작 되어야 함

기능 요구 사항

* 스프링 빈 활용하기
  * 매번 생성하지 않아도 되는 객체와 싱글톤이나 static으로 구현되었던 객체들을 스프링 빈으로 관리하기
* 스프링 JDBC 활용하기
  * Dao 객체가 아닌 DB에서 데이터를 관리하기
  * DB에 접근하기 위한 spring jdbc 라이브러리를 활용하기 (JdbcTemplate 등)
* H2 DB를 통해 저장된 값 확인하기
  * 실제로 DB에 저장이 잘 되었는지 확인할 수 있도록 (log, console 등) 설정하기

### 4단계 : 리팩터링

미션 설명

* Layered Architecture를 고려하여 리팩터링 하기

기능 요구 사항

* Layered Architecture를 활용하여 비즈니스 로직을 완전히 분리하기
* 핵심 비지니스 로직를 어느 패키지의 객체에서 구현해야 할 지 고민하기
* 패키지 구분은 어떤 기준으로 하는게 좋을 지 고민하기

---

## 실행 방법

### Install

#### npm 설치

```console
cd frontend
npm install
```

> `frontend` 디렉토리에서 수행해야 합니다.

### Usage

#### webpack server 구동

```console
npm run dev
```

#### application 구동

```console
./gradlew bootRun
```

---

## 참고 링크

### ✏️ Code Review Process

[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

### 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/spring-subway-admin-kakao/issues) 에 등록해주세요 :)

### 📝 License

This project is [MIT](https://github.com/next-step/spring-subway-admin-kakao/blob/master/LICENSE) licensed.
