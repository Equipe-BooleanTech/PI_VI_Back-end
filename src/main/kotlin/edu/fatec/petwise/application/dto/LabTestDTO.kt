package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.LabTest
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID


@Schema(description = "Data Transfer Object for Lab Test")
data class LabTestDTO(
    @Schema(description = "Unique identifier of the lab test", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID? = null,

    @Schema(description = "Name of the lab test", example = "Complete Blood Count")
    @NotNull(message = "testName cannot be null")
    val testName: String,

    @Schema(description = "Code identifier for the lab test", example = "CBC-001")
    @NotNull(message = "testCode cannot be null")
    val testCode: String,

    @Schema(description = "Detailed description of the lab test")
    @Size(max = 500, message = "description cannot exceed 500 characters")
    val description: String? = null,

    @Schema(description = "Category of the lab test", example = "HEMATOLOGY")
    @NotNull(message = "category cannot be null")
    val category: String, // HEMATOLOGY, BIOCHEMISTRY, PARASITOLOGY, VIROLOGY, BACTERIOLOGY, CYTOLOGY, URINALYSIS

    @Schema(description = "Type of sample required", example = "BLOOD")
    @NotNull(message = "sampleType cannot be null")
    val sampleType: String, // BLOOD, URINE, FECAL, TISSUE, SWAB

    @Schema(description = "Minimum normal range value", example = "4.5")
    val normalRangeMin: BigDecimal? = null,

    @Schema(description = "Maximum normal range value", example = "11.0")
    val normalRangeMax: BigDecimal? = null,

    @Schema(description = "Unit of measurement", example = "g/dL")
    @NotNull(message = "unit cannot be null")
    val unit: String,

    @Schema(description = "Duration of the test in hours", example = "24")
    @NotNull(message = "testDuration cannot be null")
    @Min(value = 0, message = "testDuration must be greater than or equal to 0")
    val testDuration: Int,

    @Schema(description = "Whether the test is currently active", example = "true")
    val isActive: Boolean = true,

    @Schema(description = "Price of the test", example = "150.00")
    @NotNull(message = "price cannot be null")
    @Min(value = 0, message = "price must be greater than or equal to 0")
    val price: BigDecimal
) {
    companion object {
        fun toEntity(dto: LabTestDTO): LabTest {
            return LabTest(
                id = dto.id,
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
                price = dto.price
            )
        }

        fun fromEntity(entity: LabTest): LabTestDTO? {
            return entity.unit?.let {
                entity.testDuration?.let { it1 ->
                    LabTestDTO(
                        id = entity.id,
                        testName = entity.testName,
                        testCode = entity.testCode,
                        description = entity.description,
                        category = entity.category,
                        sampleType = entity.sampleType,
                        normalRangeMin = entity.normalRangeMin,
                        normalRangeMax = entity.normalRangeMax,
                        unit = it,
                        testDuration = it1,
                        isActive = entity.isActive,
                        price = entity.price
                    )
                }
            }
        }
    }
}
