package edu.fatec.petwise.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["edu.fatec.petwise.infrastructure.persistence.jpa"])
class JpaConfig
