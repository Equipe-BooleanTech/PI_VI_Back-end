package edu.fatec.petwise.presentation.exception

import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.DuplicateEntityException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.exception.InvalidEntityException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: String = LocalDateTime.now().toString(),
    val status: Int,
    val error: String,
    val message: String?,
    val path: String? = null,
    val validationErrors: Map<String, String>? = null
)

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Entidade não encontrada: ${ex.message}")
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Não encontrado",
            message = ex.message ?: "Recurso não encontrado"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(DuplicateEntityException::class)
    fun handleDuplicateEntityException(ex: DuplicateEntityException): ResponseEntity<ErrorResponse> {
        logger.warn("Entidade duplicada: ${ex.message}")
        val error = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Conflito",
            message = ex.message ?: "Recurso já existe"
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(BusinessRuleException::class)
    fun handleBusinessRuleException(ex: BusinessRuleException): ResponseEntity<ErrorResponse> {
        logger.warn("Violação de regra de negócio: ${ex.message}")
        val error = ErrorResponse(
            status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
            error = "Regra de negócio violada",
            message = ex.message ?: "Operação não permitida"
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error)
    }

    @ExceptionHandler(InvalidEntityException::class)
    fun handleInvalidEntityException(ex: InvalidEntityException): ResponseEntity<ErrorResponse> {
        logger.warn("Entidade inválida: ${ex.message}")
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Requisição inválida",
            message = ex.message ?: "Dados inválidos"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        logger.warn("Erro de validação: ${ex.message}")
        val errors = ex.bindingResult.allErrors.associate {
            val fieldName = (it as? FieldError)?.field ?: "unknown"
            val errorMessage = it.defaultMessage ?: "Inválido"
            fieldName to errorMessage
        }
        
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Erro de validação",
            message = "Um ou mais campos estão inválidos",
            validationErrors = errors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        logger.warn("Estado ilegal: ${ex.message}")
        val error = ErrorResponse(
            status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
            error = "Operação não permitida",
            message = ex.message ?: "Operação não pode ser realizada"
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Erro interno do servidor: ${ex.message}", ex)
        
        // For specific business logic errors, return the actual message
        val message = when {
            ex.message?.contains("Pet não encontrado") == true -> ex.message
            ex.message?.contains("Você não tem permissão") == true -> ex.message
            ex.message?.contains("Não é possível remover o pet") == true -> ex.message
            else -> "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde."
        }
        
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Erro interno do servidor",
            message = message
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
