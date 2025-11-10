# Spring Boot DDD 예제 프로젝트

## 프로젝트 개요

이 프로젝트는 Spring Boot, Gradle을 사용한 DDD(Domain-Driven Design) 구조의 예제 애플리케이션입니다.
상품(Product) 관리 시스템을 통해 DDD의 핵심 개념들을 실습할 수 있습니다.

## 기술 스택

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Build Tool**: Gradle
- **ORM**: JPA (Hibernate)
- **Query**: QueryDSL 5.0.0
- **Database**: MySQL (운영), H2 (테스트)
- **Testing**: Spock Framework 2.3
- **기타**: Lombok

## 프로젝트 구조 (DDD)

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── ExampleApplication.java
│   │   ├── domain/                    # 도메인 계층
│   │   │   └── product/
│   │   │       ├── Product.java       # 엔티티 (Aggregate Root)
│   │   │       ├── Money.java         # Value Object
│   │   │       ├── ProductStatus.java # Enum
│   │   │       └── ProductRepository.java  # Repository Interface
│   │   ├── application/               # 애플리케이션 계층
│   │   │   └── product/
│   │   │       ├── ProductService.java     # Application Service
│   │   │       └── dto/
│   │   │           ├── ProductRequest.java
│   │   │           └── ProductResponse.java
│   │   ├── infrastructure/            # 인프라 계층
│   │   │   ├── config/
│   │   │   │   └── QueryDslConfig.java
│   │   │   └── product/
│   │   │       ├── ProductJpaRepository.java      # JPA Repository
│   │   │       └── ProductRepositoryImpl.java     # Repository 구현
│   │   └── presentation/              # 프레젠테이션 계층
│   │       ├── product/
│   │       │   └── ProductController.java         # REST API Controller
│   │       └── common/
│   │           └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.yml
└── test/
    ├── groovy/com/example/            # Spock 테스트
    │   ├── domain/product/
    │   │   └── ProductSpec.groovy
    │   ├── application/product/
    │   │   └── ProductServiceSpec.groovy
    │   └── infrastructure/product/
    │       └── ProductRepositoryImplSpec.groovy
    └── resources/
        └── application.yml
```

## DDD 계층 설명

### 1. Domain Layer (도메인 계층)
- **Product**: 상품 엔티티 (Aggregate Root)
- **Money**: 금액을 표현하는 Value Object
- **ProductStatus**: 상품 상태를 나타내는 Enum
- **ProductRepository**: Repository 인터페이스 (도메인 규칙에 따른)

### 2. Application Layer (애플리케이션 계층)
- **ProductService**: 유스케이스를 구현하는 애플리케이션 서비스
- **DTO**: 요청/응답을 위한 데이터 전송 객체

### 3. Infrastructure Layer (인프라 계층)
- **ProductJpaRepository**: Spring Data JPA Repository
- **ProductRepositoryImpl**: QueryDSL을 사용한 Repository 구현
- **QueryDslConfig**: QueryDSL 설정

### 4. Presentation Layer (프레젠테이션 계층)
- **ProductController**: REST API 엔드포인트
- **GlobalExceptionHandler**: 전역 예외 처리

## 주요 기능

### 상품 관리 API

- `POST /api/products` - 상품 생성
- `GET /api/products/{id}` - 상품 조회
- `GET /api/products` - 모든 상품 조회
- `GET /api/products/available` - 판매 가능한 상품 조회
- `GET /api/products/search?name={name}` - 상품명으로 검색
- `PUT /api/products/{id}` - 상품 수정
- `POST /api/products/{id}/stock/add?quantity={quantity}` - 재고 추가
- `POST /api/products/{id}/stock/remove?quantity={quantity}` - 재고 감소
- `PATCH /api/products/{id}/status?status={status}` - 상품 상태 변경
- `DELETE /api/products/{id}` - 상품 삭제

## 설치 및 실행

### 사전 요구사항

- JDK 17 이상
- MySQL 8.0 이상 (운영 환경)

### 데이터베이스 설정

```sql
-- MySQL 데이터베이스 생성
CREATE DATABASE example CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 애플리케이션 실행

```bash
# 프로젝트 클론 후
cd example

# Gradle 빌드 (QueryDSL Q클래스 생성 포함)
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

### 테스트 실행

```bash
# 모든 테스트 실행 (Spock)
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests "com.example.domain.product.ProductSpec"
```

## QueryDSL 설정

QueryDSL Q클래스는 빌드 시 자동으로 생성됩니다:

```bash
# Q클래스 생성
./gradlew compileJava
```

생성된 Q클래스는 `build/generated/querydsl` 디렉토리에 위치합니다.

## 환경 설정

### application.yml (운영)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

### application.yml (테스트)

테스트 환경에서는 H2 인메모리 데이터베이스를 사용합니다.

## API 사용 예제

### 상품 생성

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "노트북",
    "price": 1500000,
    "description": "고성능 노트북",
    "stockQuantity": 50
  }'
```

### 상품 조회

```bash
curl http://localhost:8080/api/products/1
```

### 판매 가능한 상품 조회

```bash
curl http://localhost:8080/api/products/available
```

### 상품 검색

```bash
curl "http://localhost:8080/api/products/search?name=노트북"
```

## 도메인 모델 특징

### Product (상품 엔티티)
- Aggregate Root로서 상품 관련 모든 비즈니스 로직 포함
- 불변성을 보장하기 위한 Setter 미사용
- 도메인 로직: 재고 관리, 상태 변경, 유효성 검증 등

### Money (Value Object)
- 금액을 표현하는 불변 객체
- 금액 계산 로직 캡슐화
- 동등성 비교를 위한 equals/hashCode 구현

### 비즈니스 규칙
- 상품명은 필수
- 가격은 0보다 커야 함
- 재고는 0 이상이어야 함
- 재고 부족 시 판매 불가
- 상품 상태에 따른 판매 가능 여부 결정

## 테스트 전략

### Unit Test (Spock)
- **ProductSpec**: 도메인 모델의 비즈니스 로직 테스트
- **ProductServiceSpec**: 애플리케이션 서비스 로직 테스트 (Mock 사용)

### Integration Test
- **ProductRepositoryImplSpec**: Repository 통합 테스트 (실제 DB 사용)

## 개발자 참고사항

### QueryDSL 사용 시 주의사항
- 엔티티 수정 후 반드시 `./gradlew compileJava`로 Q클래스 재생성
- Q클래스는 Git에 커밋하지 않음 (.gitignore에 포함됨)

### DDD 설계 원칙
- 도메인 모델은 기술에 독립적
- Repository 인터페이스는 도메인 계층에 정의
- 비즈니스 로직은 도메인 모델에 집중
- 애플리케이션 서비스는 트랜잭션과 도메인 조합만 담당

## 향후 개선 사항

- [ ] 주문(Order) Aggregate 추가
- [ ] 이벤트 기반 아키텍처 적용
- [ ] CQRS 패턴 적용
- [ ] API 문서화 (Swagger/OpenAPI)
- [ ] 보안 (Spring Security) 적용
- [ ] 로깅 및 모니터링 강화

## 라이센스

이 프로젝트는 학습 목적의 예제 프로젝트입니다.
