package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.LabTestDTO
import edu.fatec.petwise.domain.entity.LabTest
import edu.fatec.petwise.domain.repository.LabTestRepository
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.util.UUID
import java.util.Optional
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ManageLabTestsUseCase @Autowired constructor(
    private val labTestRepository: LabTestRepository
) {
    private val logger = LoggerFactory.getLogger(ManageLabTestsUseCase::class.java)

    fun getAllTests(page: Int = 0, size: Int = 20): List<LabTestDTO> {
        return try {
            val pageable = PageRequest.of(page, size)
            labTestRepository.findAll(pageable).content.map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar todos os exames: ${e.message}")
            throw RuntimeException("Falha ao recuperar exames", e)
        }
    }

    fun getTestsByCategory(category: String): List<LabTestDTO> {
        return try {
            labTestRepository.findByCategory(category).map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar exames por categoria $category: ${e.message}")
            throw RuntimeException("Falha ao recuperar exames por categoria", e)
        }
    }

    fun getActiveTests(): List<LabTestDTO> {
        return try {
            labTestRepository.findByIsActiveTrueOrderByTestName().map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar exames ativos: ${e.message}")
            throw RuntimeException("Falha ao recuperar exames ativos", e)
        }
    }

    fun createTest(dto: LabTestDTO): LabTestDTO {
        return try {
            logger.info("Criando novo exame: ${dto.testName}")
            
            val entity = dto.toEntity()
            val saved = labTestRepository.save(entity)
            logger.info("Exame criado com sucesso: ${saved.id}")
            
            saved.toDTO()
        } catch (e: DataIntegrityViolationException) {
            logger.error("Erro de integridade ao criar exame: ${e.message}")
            throw IllegalArgumentException("Código de exame já existe ou dados inválidos", e)
        } catch (e: Exception) {
            logger.error("Erro ao criar exame: ${e.message}")
            throw RuntimeException("Falha ao criar exame", e)
        }
    }

    fun updateTest(id: UUID, dto: LabTestDTO): LabTestDTO {
        return try {
            logger.info("Atualizando exame: $id")
            
            val existing = labTestRepository.findById(id)
                .orElseThrow { NoSuchElementException("Exame não encontrado: $id") }
            
            val updated = existing.copy(
                testName = dto.testName,
                testCode = dto.testCode,
                description = dto.description,
                category = dto.category,
                sampleType = dto.sampleType,
                normalRangeMin = dto.normalRangeMin,
                normalRangeMax = dto.normalRangeMax,
                unit = dto.unit,
                testDuration = dto.testDuration,
                isActive = dto.isActive,
                price = dto.price,
                updatedAt = java.time.LocalDateTime.now()
            )
            
            val saved = labTestRepository.save(updated)
            logger.info("Exame atualizado com sucesso: ${saved.id}")
            
            saved.toDTO()
        } catch (e: NoSuchElementException) {
            logger.error("Exame não encontrado para atualização: $id")
            throw NoSuchElementException("Exame não encontrado: $id")
        } catch (e: Exception) {
            logger.error("Erro ao atualizar exame $id: ${e.message}")
            throw RuntimeException("Falha ao atualizar exame", e)
        }
    }

    fun deleteTest(id: UUID): Boolean {
        return try {
            logger.info("Removendo exame: $id")
            
            if (!labTestRepository.existsById(id)) {
                throw NoSuchElementException("Exame não encontrado: $id")
            }
            
            labTestRepository.deleteById(id)
            logger.info("Exame removido com sucesso: $id")
            true
        } catch (e: NoSuchElementException) {
            logger.error("Exame não encontrado para remoção: $id")
            throw e
        } catch (e: Exception) {
            logger.error("Erro ao remover exame $id: ${e.message}")
            throw RuntimeException("Falha ao remover exame", e)
        }
    }

    fun getTestByCode(testCode: String): Optional<LabTestDTO> {
        return try {
            labTestRepository.findByTestCode(testCode).map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar exame por código $testCode: ${e.message}")
            throw RuntimeException("Falha ao buscar exame por código", e)
        }
    }

    fun searchTests(query: String): List<LabTestDTO> {
        return try {
            labTestRepository.findByTestNameContainingIgnoreCase(query).map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar exames com query '$query': ${e.message}")
            throw RuntimeException("Falha ao buscar exames", e)
        }
    }

    fun getMostPopularTests(): List<LabTestDTO> {
        return try {
            // Como não temos campo de popularidade, retornamos os mais recentes ativos
            labTestRepository.findByIsActiveTrueOrderByTestName()
                .take(10)
                .map { it.toDTO() }
        } catch (e: Exception) {
            logger.error("Erro ao buscar exames populares: ${e.message}")
            throw RuntimeException("Falha ao recuperar exames populares", e)
        }
    }

    fun getCategoryStatistics(): Map<String, Any> {
        return try {
            logger.info("Gerando estatísticas por categoria")
            
            val categories = labTestRepository.findAll()
                .groupBy { it.category }
                .mapValues { (_, tests) ->
                    mapOf(
                        "total" to tests.size,
                        "active" to tests.count { it.isActive },
                        "averagePrice" to tests.filter { it.price != null }
                            .map { it.price!! }
                            .average(),
                        "avgDuration" to tests.filter { it.testDuration != null }
                            .map { it.testDuration!! }
                            .average()
                    )
                }
            
            categories
        } catch (e: Exception) {
            logger.error("Erro ao gerar estatísticas: ${e.message}")
            throw RuntimeException("Falha ao gerar estatísticas", e)
        }
    }

    private fun LabTest.toDTO(): LabTestDTO {
        return LabTestDTO.fromEntity(this)
    }

    private fun LabTestDTO.toEntity(): LabTest {
        return LabTestDTO.toEntity()
    }
}
