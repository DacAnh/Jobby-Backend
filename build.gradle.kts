plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
    id("io.freefair.lombok") version "8.6"
}

group = "vn.hoidanit"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

//	Kích hoạt cơ chế oauth2 để xác thực thông tin người dùng thông qua token
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

//	Cài đặt thư viện do community phát triển nhằm mục đích filter theo từng tiêu chí
//	khác với viết thủ công từng predicate (Specification) ứng với từng tiêu chí filter
//	Tuy nhiên việc dùng thư viện bên ngoài, bắt buộc phải tuân theo quy tắc của thư viện đó
	implementation("com.turkraft.springfilter:jpa:3.1.7")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
