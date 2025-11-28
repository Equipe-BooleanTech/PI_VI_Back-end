plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("plugin.noarg") version "1.9.25"
	kotlin("plugin.serialization") version "1.9.25"
	jacoco // Plugin para cobertura de testes
}

group = "edu.fatec"
version = "0.0.1-SNAPSHOT"
description = "Projeto Interdisciplinar final (VI) do curso de Desenvolvimento de Software Multiplataforma"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-mail")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

	implementation("org.modelmapper:modelmapper:3.2.0")
	implementation("me.paulschwarz:spring-dotenv:4.0.0")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

	// ✅ CORREÇÃO 5: Rate Limiting com Bucket4j
	implementation("com.bucket4j:bucket4j-core:8.10.1")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	runtimeOnly("org.postgresql:postgresql")


	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

noArg {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

// ============================================================================
// CONFIGURAÇÃO DE TESTES
// ============================================================================

tasks.withType<Test> {
	useJUnitPlatform()
	
	// Configuração de logs e relatórios de teste
	testLogging {
		events("passed", "skipped", "failed")
		showExceptions = true
		showCauses = true
		showStackTraces = true
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
	}
	
	// Relatório HTML de testes
	reports {
		html.required.set(true)
		junitXml.required.set(true)
	}
	
	finalizedBy(tasks.jacocoTestReport)
}

// Task para executar apenas testes do caso de uso Pet
tasks.register<Test>("testPet") {
	description = "Executa apenas os testes do caso de uso Pet"
	group = "verification"
	
	useJUnitPlatform()
	
	filter {
		includeTestsMatching("edu.fatec.petwise.pets.*")
	}
	
	testLogging {
		events("passed", "skipped", "failed")
		showExceptions = true
		showCauses = true
		showStackTraces = true
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
	}
	
	reports {
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("reports/tests/pet"))
		junitXml.required.set(true)
		junitXml.outputLocation.set(layout.buildDirectory.dir("test-results/pet"))
	}
	
	finalizedBy("jacocoTestReportPet")
}

// ============================================================================
// CONFIGURAÇÃO DO JACOCO - COBERTURA DE TESTES
// ============================================================================

jacoco {
	toolVersion = "0.8.11"
}

// Relatório de cobertura geral
tasks.jacocoTestReport {
	dependsOn(tasks.test)
	
	reports {
		xml.required.set(true)
		csv.required.set(true)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
	}
	
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(
					"**/dto/**",
					"**/config/**",
					"**/entity/**",
					"**/enums/**",
					"**/exception/**",
					"**/valueobject/**",
					"**/infrastructure/**",
					"**/presentation/**",
					"**/*Application*",
					"**/*Config*",
					"**/*Request*",
					"**/*Response*"
				)
			}
		})
	)
}

// Relatório de cobertura específico para Pet
tasks.register<JacocoReport>("jacocoTestReportPet") {
	dependsOn("testPet")
	
	description = "Gera relatório de cobertura para os testes do caso de uso Pet"
	group = "verification"
	
	executionData.setFrom(fileTree(layout.buildDirectory).include("jacoco/testPet.exec"))
	
	sourceDirectories.setFrom(files("src/main/kotlin"))
	classDirectories.setFrom(
		files(layout.buildDirectory.dir("classes/kotlin/main")).asFileTree.matching {
			include(
				"**/usecase/*Pet*.class",
				"**/usecase/Create*UseCase.class",
				"**/usecase/Update*UseCase.class",
				"**/usecase/Delete*UseCase.class",
				"**/usecase/Get*UseCase.class",
				"**/usecase/Filter*UseCase.class",
				"**/usecase/Toggle*UseCase.class",
				"**/usecase/Search*UseCase.class"
			)
		}
	)
	
	reports {
		xml.required.set(true)
		csv.required.set(true)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/pet"))
	}
}

// Verificação de cobertura mínima
tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.60".toBigDecimal()
			}
		}
		
		rule {
			element = "CLASS"
			includes = listOf("edu.fatec.petwise.application.usecase.*")
			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.70".toBigDecimal()
			}
		}
	}
}

// Task para executar testes e gerar relatório completo
tasks.register("testWithReport") {
	description = "Executa todos os testes e gera relatório de cobertura"
	group = "verification"
	
	dependsOn("test", "jacocoTestReport")
}

// Task para executar testes Pet e gerar relatório
tasks.register("testPetWithReport") {
	description = "Executa testes do Pet e gera relatório de cobertura"
	group = "verification"
	
	dependsOn("testPet", "jacocoTestReportPet")
}
