package edu.fatec.petwise.application.validation

import edu.fatec.petwise.application.validation.SupplierValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * Anotação de validação personalizada para SupplierDTO.
 * Valida regras de negócio específicas para fornecedores,
 * como a obrigatoriedade de pelo menos um documento (CNPJ ou CPF).
 *
 * @author Sistema PetWise
 * @since 1.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [SupplierValidator::class])
annotation class SupplierValidation(
    val message: String = "Validação de fornecedor falhou",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)