package edu.fatec.petwise

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PetwiseApplication

fun main(args: Array<String>) {
	runApplication<PetwiseApplication>(*args)
}
