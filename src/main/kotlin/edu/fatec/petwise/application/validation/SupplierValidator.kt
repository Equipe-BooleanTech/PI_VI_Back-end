package edu.fatec.petwise.application.validation

import edu.fatec.petwise.application.dto.SupplierDTO
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


/**
 * Implementação do validador para a anotação @SupplierValidation.
 * Este validador executa validações de regras de negócio específicas para fornecedores.
 *
 * Validações implementadas:
 * - Pelo menos um documento (CNPJ ou CPF) deve ser fornecido
 * - Não permitir que ambos CNPJ e CPF sejam fornecidos simultaneamente
 *
 * @author Sistema PetWise
 * @since 1.0
 */
class SupplierValidator : ConstraintValidator<SupplierValidation, SupplierDTO> {

    override fun initialize(constraint: SupplierValidation) {
        // Inicialização do validador, se necessário
    }

    override fun isValid(value: SupplierDTO?, context: ConstraintValidatorContext): Boolean {
        // Se o valor for nulo, a validação passa (outros validators devem tratar nulidade)
        if (value == null) {
            return true
        }

        val hasCnpj = !value.cnpj.isNullOrBlank()
        val hasCpf = !value.cpf.isNullOrBlank()

        // Regra 1: Pelo menos um documento deve ser fornecido
        if (!hasCnpj && !hasCpf) {
            // Adiciona violação ao contexto
            context.buildConstraintViolationWithTemplate("Pelo menos um documento (CNPJ ou CPF) deve ser fornecido")
                .addConstraintViolation()
            return false
        }

        // Regra 2: Não permitir ambos CNPJ e CPF simultaneamente
        if (hasCnpj && hasCpf) {
            // Adiciona violação ao contexto
            context.buildConstraintViolationWithTemplate("Apenas um documento (CNPJ ou CPF) deve ser fornecido, não ambos")
                .addConstraintViolation()
            return false
        }

        // Todas as validações passaram
        return true
    }
}