package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.domain.repository.LabSampleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.util.*


@Service
class ManageLabSamplesUseCase @Autowired constructor(
    private val labSampleRepository: LabSampleRepository
) {

    private val logger = LoggerFactory.getLogger(ManageLabSamplesUseCase::class.java)

    fun getAllSamples(page: Int = 0, size: Int = 20): List<LabSampleDTO> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val samples = labSampleRepository.findAll(pageable)
            logger.info("Obtenção de amostras realizada com sucesso - página: $page, tamanho: $size")
            samples.content.map { it.toDTO() }
        } catch (ex: Exception) {
            logger.error("Erro ao obter todas as amostras - página: $page, tamanho: $size", ex)
            throw ex
        }
    }

    fun getSamplesByPetId(petId: UUID): List<LabSampleDTO> {
        return try {
            val samples = labSampleRepository.findByPetId(petId)
            logger.info("Obtenção de amostras por pet ID $petId realizada com sucesso - total: ${samples.size}")
            samples.map { it.toDTO() }
        } catch (ex: Exception) {
            logger.error("Erro ao obter amostras por pet ID: $petId", ex)
            throw ex
        }
    }

    fun getSamplesByStatus(status: String): List<LabSampleDTO> {
        return try {
            if (status.isBlank()) {
                throw IllegalArgumentException("Status não pode ser vazio")
            }
            val samples = labSampleRepository.findByStatus(status)
            logger.info("Obtenção de amostras por status '$status' realizada com sucesso - total: ${samples.size}")
            samples.map { it.toDTO() }
        } catch (ex: IllegalArgumentException) {
            logger.warn("Erro de argumento inválido ao obter amostras por status: $status", ex)
            throw ex
        } catch (ex: Exception) {
            logger.error("Erro ao obter amostras por status: $status", ex)
            throw ex
        }
    }

    fun createSample(dto: LabSampleDTO): LabSampleDTO {
        return try {
            if (dto.sampleCode.isNullOrBlank()) {
                throw IllegalArgumentException("Código da amostra é obrigatório")
            }
            
            val existingSample = labSampleRepository.findBySampleCode(dto.sampleCode)
            if (existingSample.isPresent) {
                throw IllegalArgumentException("Código da amostra '${dto.sampleCode}' já existe")
            }

            val sample = dto.toEntity()
            val savedSample = labSampleRepository.save(sample)
            logger.info("Amostra criada com sucesso - código: ${savedSample.sampleCode}")
            savedSample.toDTO()
        } catch (ex: IllegalArgumentException) {
            logger.warn("Erro de argumento inválido ao criar amostra: ${dto.sampleCode}", ex)
            throw ex
        } catch (ex: Exception) {
            logger.error("Erro ao criar amostra: ${dto.sampleCode}", ex)
            throw ex
        }
    }

    fun updateSample(id: UUID, dto: LabSampleDTO): LabSampleDTO {
        return try {
            val existingSample = labSampleRepository.findById(id)
                .orElseThrow { 
                    logger.warn("Amostra não encontrada para atualização - ID: $id")
                    throw EntityNotFoundException("Amostra não encontrada com ID: $id")
                }

            if (dto.sampleCode != existingSample.sampleCode) {
                val duplicateSample = labSampleRepository.findBySampleCode(dto.sampleCode)
                if (duplicateSample.isPresent) {
                    throw IllegalArgumentException("Código da amostra '${dto.sampleCode}' já existe")
                }
            }

            val updatedSample = dto.toEntity().apply {
                this.id = id
            }
            val savedSample = labSampleRepository.save(updatedSample)
            logger.info("Amostra atualizada com sucesso - ID: $id, código: ${savedSample.sampleCode}")
            savedSample.toDTO()
        } catch (ex: EntityNotFoundException) {
            logger.warn("Erro ao atualizar amostra - ID: $id não encontrada", ex)
            throw ex
        } catch (ex: IllegalArgumentException) {
            logger.warn("Erro de argumento inválido ao atualizar amostra - ID: $id", ex)
            throw ex
        } catch (ex: Exception) {
            logger.error("Erro ao atualizar amostra - ID: $id", ex)
            throw ex
        }
    }

    fun deleteSample(id: UUID): Boolean {
        return try {
            val existingSample = labSampleRepository.findById(id)
                .orElseThrow { 
                    logger.warn("Amostra não encontrada para exclusão - ID: $id")
                    throw EntityNotFoundException("Amostra não encontrada com ID: $id")
                }

            labSampleRepository.delete(existingSample)
            logger.info("Amostra deletada com sucesso - ID: $id, código: ${existingSample.sampleCode}")
            true
        } catch (ex: EntityNotFoundException) {
            logger.warn("Erro ao deletar amostra - ID: $id não encontrada", ex)
            throw ex
        } catch (ex: Exception) {
            logger.error("Erro ao deletar amostra - ID: $id", ex)
            throw ex
        }
    }

    fun getSampleByCode(sampleCode: String): Optional<LabSampleDTO> {
        return try {
            if (sampleCode.isBlank()) {
                throw IllegalArgumentException("Código da amostra não pode ser vazio")
            }
            val sample = labSampleRepository.findBySampleCode(sampleCode)
            logger.info("Busca por código de amostra '$sampleCode' realizada com sucesso")
            sample.map { it.toDTO() }
        } catch (ex: IllegalArgumentException) {
            logger.warn("Erro de argumento inválido ao buscar amostra por código: $sampleCode", ex)
            throw ex
        } catch (ex: Exception) {
            logger.error("Erro ao buscar amostra por código: $sampleCode", ex)
            throw ex
        }
    }

    fun updateSampleStatus(id: UUID, status: String): LabSampleDTO {
        return try {
            if (status.isBlank()) {
                throw IllegalArgumentException("Status não pode ser vazio")
            }

            val existingSample = labSampleRepository.findById(id)
                .orElseThrow { 
                    logger.warn("Amostra não encontrada para atualização de status - ID: $id")
                    throw EntityNotFoundException("Amostra não encontrada com ID: $id")
                }

            existingSample.status = status
            val updatedSample = labSampleRepository.save(existingSample)
            logger.info("Status da amostra atualizado com sucesso - ID: $id, novo status: $status")
            updatedSample.toDTO()
        } catch (ex: EntityNotFoundException) {
            logger.warn("Erro ao atualizar status - amostra ID: $id não encontrada", ex)
            throw ex
        } catch (ex: IllegalArgumentException) {
            logger.warn("Erro de argumento inválido ao atualizar status - ID: $id, status: $status", ex)
            throw ex
        } catch (ex: Exception) {
            logger.error("Erro ao atualizar status da amostra - ID: $id, status: $status", ex)
            throw ex
        }
    }

    fun getExpiredSamples(): List<LabSampleDTO> {
        return try {
            val samples = labSampleRepository.findExpiredSamples()
            logger.info("Obtenção de amostras expiradas realizada com sucesso - total: ${samples.size}")
            samples.map { it.toDTO() }
        } catch (ex: Exception) {
            logger.error("Erro ao obter amostras expiradas", ex)
            throw ex
        }
    }

    fun getSamplesByTechnicianId(technicianId: UUID): List<LabSampleDTO> {
        return try {
            val samples = labSampleRepository.findByTechnicianId(technicianId)
            logger.info("Obtenção de amostras por técnico ID $technicianId realizada com sucesso - total: ${samples.size}")
            samples.map { it.toDTO() }
        } catch (ex: Exception) {
            logger.error("Erro ao obter amostras por técnico ID: $technicianId", ex)
            throw ex
        }
    }

    fun getSampleStatistics(): Map<String, Any> {
        return try {
            val totalSamples = labSampleRepository.count()
            val samplesByStatus = labSampleRepository.getSamplesCountByStatus()
            val expiredSamples = labSampleRepository.findExpiredSamples().size
            val recentSamples = labSampleRepository.findRecentSamples(Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000)).size

            val statistics = mapOf(
                "totalSamples" to totalSamples,
                "samplesByStatus" to samplesByStatus,
                "expiredSamples" to expiredSamples,
                "recentSamples" to recentSamples,
                "calculatedAt" to System.currentTimeMillis()
            )

            logger.info("Estatísticas de amostras obtidas com sucesso")
            statistics
        } catch (ex: Exception) {
            logger.error("Erro ao obter estatísticas das amostras", ex)
            throw ex
        }
    }
}