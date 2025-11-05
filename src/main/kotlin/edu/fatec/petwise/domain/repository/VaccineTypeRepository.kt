package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.VaccineType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VaccineTypeRepository : JpaRepository<VaccineType, UUID> {
    
    fun findBySpeciesAndActiveTrueOrderByVaccineName(species: String): List<VaccineType>
    
    fun findByActiveTrueOrderBySpeciesVaccineName(): List<VaccineType>
    
    fun findByIdAndActiveTrue(id: UUID): VaccineType?
    
    @Query("SELECT DISTINCT vt.species FROM VaccineType vt WHERE vt.active = true ORDER BY vt.species")
    fun findDistinctSpecies(): List<String>
    
    @Query("SELECT vt FROM VaccineType vt WHERE vt.species = :species AND vt.active = true ORDER BY vt.vaccineName")
    fun findBySpecies(@Param("species") species: String): List<VaccineType>
    
    @Query("SELECT vt FROM VaccineType vt WHERE vt.species = :species AND LOWER(vt.vaccineName) LIKE LOWER(CONCAT('%', :vaccineName, '%')) AND vt.active = true")
    fun findBySpeciesAndVaccineNameContaining(@Param("species") species: String, @Param("vaccineName") vaccineName: String): List<VaccineType>
    
    @Query("SELECT COUNT(vt) FROM VaccineType vt WHERE vt.species = :species AND vt.active = true")
    fun countBySpecies(@Param("species") species: Long): Long
}