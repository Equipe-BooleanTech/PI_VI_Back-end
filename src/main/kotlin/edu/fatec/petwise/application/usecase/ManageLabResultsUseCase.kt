package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.LabResultDTO
import edu.fatec.petwise.domain.repository.LabResultRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import java.util.*
import java.time.LocalDateTime
import kotlin.IllegalArgumentException

@Service
@Slf4j
class ManageLabResultsUseCase {
    
    @Autowired
    private lateinit var labResultRepository: LabResultRepository
    
    fun getAllResults(page: Int = 0, size: Int = 20): List<LabResultDTO> {
        log.info("Buscando todos os resultados de exames - página: $page, tamanho: $size")
        return try {
            val results = labResultRepository.findAll(page, size)
            log.info("Encontrados ${results.size} resultados de exames")
            results
        } catch (e: Exception) {
            log.error("Erro ao buscar todos os resultados de exames", e)
            throw e
        }
    }
    
    fun getResultsByPetId(petId: UUID): List<LabResultDTO> {
        log.info("Buscando resultados de exames para o pet: $petId")
        return try {
            if (petId == null) {
                throw IllegalArgumentException("Pet ID não pode ser nulo")
            }
            val results = labResultRepository.findByPetId(petId)
            log.info("Encontrados ${results.size} resultados de exames para o pet $petId")
            results
        } catch (e: IllegalArgumentException) {
            log.error("Argumento inválido ao buscar resultados por pet ID: $petId", e)
            throw e
        } catch (e: Exception) {
            log.error("Erro ao buscar resultados de exames por pet ID: $petId", e)
            throw e
        }
    }
    
    fun getResultsByTestId(testId: UUID): List<LabResultDTO> {
        log.info("Buscando resultados de exames para o teste: $testId")
        return try {
            val results = labResultRepository.findByTestId(testId)
            log.info("Encontrados ${results.size} resultados para o teste $testId")
            results
        } catch (e: IllegalArgumentException) {
            log.error("Argumento inválido ao buscar resultados por test ID: $testId", e)
            throw e
        } catch (e: Exception) {
            log.error("Erro ao buscar resultados de exames por test ID: $testId", e)
            throw e
        }
    }
    
    fun createResult(dto: LabResultDTO): LabResultDTO {
        log.info("Criando novo resultado de exame para o teste: ${dto.testId}")
        return try {
            if (dto == null) {
                throw IllegalArgumentException("DTO não pode ser nulo")
            }
            if (dto.testId == null) {
                throw IllegalArgumentException("Test ID é obrigatório")
            }
            if (dto.petId == null) {
                throw IllegalArgumentException("Pet ID é obrigatório")
            }
            if (dto.result == null || dto.result.isBlank()) {
                throw IllegalArgumentException("Resultado é obrigatório")
            }
            val result = labResultRepository.save(dto)
            log.info("Resultado de exame criado com sucesso - ID: ${result.id}")
            result
        } catch (e: IllegalArgumentException) {
            log.error("Argumento inválido ao criar resultado de exame", e)
            throw e
        } catch (e: Exception) {
            log.error("Erro ao criar resultado de exame", e)
            throw e
        }
    }
    
    fun updateResult(id: UUID, dto: LabResultDTO): LabResultDTO {
        log.info("Atualizando resultado de exame - ID: $id")
        return try {
            if (id == null) {
                throw IllegalArgumentException("ID não pode ser nulo")
            }
            if (dto == null) {
                throw IllegalArgumentException("DTO não pode ser nulo")
            }
            
            val existingResult = labResultRepository.findById(id)
                .orElseThrow { EntityNotFoundException("Resultado de exame não encontrado com ID: $id") }
            
            val updatedResult = dto.copy(id = id, createdAt = existingResult.createdAt)
            val savedResult = labResultRepository.save(updatedResult)
            log.info("Resultado de exame atualizado com sucesso - ID: $id")
            savedResult
        } catch (e: EntityNotFoundException) {
            log.error("Resultado de exame não encontrado para atualização - ID: $id", e)
            throw e
        } catch (e: IllegalArgumentException) {
            log.error("Argumento inválido ao atualizar resultado de exame - ID: $id", e)
            throw e
        } catch (e: Exception) {
            log.error("Erro ao atualizar resultado de exame - ID: $id", e)
            throw e
        }
    }
    
    fun deleteResult(id: UUID): Boolean {
        log.info("Deletando resultado de exame - ID: $id")
        return try {
            if (id == null) {
                throw IllegalArgumentException("ID não pode ser nulo")
            }
            
            val resultExists = labResultRepository.findById(id).isPresent
            if (!resultExists) {
                throw EntityNotFoundException("Resultado de exame não encontrado com ID: $id")
            }
            
            labResultRepository.deleteById(id)
            log.info("Resultado de exame deletado com sucesso - ID: $id")
            true
        } catch (e: EntityNotFoundException) {
            log.error("Resultado de exame não encontrado para deleção - ID: $id", e)
            throw e
        } catch (e: IllegalArgumentException) {
            log.error("Argumento inválido ao deletar resultado de exame - ID: $id", e)
            throw e
        } catch (e: Exception) {
            log.error("Erro ao deletar resultado de exame - ID: $id", e)
            throw e
        }
    }
    
    fun getAbnormalResults(): List<LabResultDTO> {
        log.info("Buscando resultados anormais de exames")
        return try {
            val results = labResultRepository.findAbnormalResults()
            log.info("Encontrados ${results.size} resultados anormais")
            results
        } catch (e: Exception) {
            log.error("Erro ao buscar resultados anormais", e)
            throw e
        }
    }
    
    fun getResultsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<LabResultDTO> {
        log.info("Buscando resultados de exames no período de $startDate a $endDate")
        return try {
            if (startDate == null || endDate == null) {
                throw IllegalArgumentException("Data de início e fim são obrigatórias")
            }
            if (startDate.isAfter(endDate)) {
                throw IllegalArgumentException("Data de início deve ser anterior à data de fim")
            }
            val results = labResultRepository.findByCreatedAtBetween(startDate, endDate)
            log.info("Encontrados ${results.size} resultados de exames no período especificado")
            results
        } catch (e: IllegalArgumentException) {
            log.error("Argumento inválido ao buscar resultados por período - início: $startDate, fim: $endDate", e)
            throw e
        } catch (e: Exception) {
            log.error("Erro ao buscar resultados de exames por período - início: $startDate, fim: $endDate", e)
            throw e
        }
    }
    
    fun getStatistics(): Map<String, Any> {
        log.info("Calculando estatísticas dos resultados de exames")
        return try {
            val stats = mapOf(
                "totalResults" to labResultRepository.count(),
                "abnormalResults" to labResultRepository.countAbnormalResults(),
                "testsByType" to labResultRepository.getTestsByType(),
                "averageResultsByMonth" to labResultRepository.getAverageResultsByMonth(),
                "lastUpdated" to LocalDateTime.now()
            )
            log.info("Estatísticas calculadas: ${stats.size} categorias")
            stats
        } catch (e: Exception) {
            log.error("Erro ao calcular estatísticas dos resultados de exames", e)
            throw e
        }
    }
}