package edu.fatec.petwise.domain.exception

sealed class DomainException(message: String) : RuntimeException(message)

class EntityNotFoundException(entityName: String, id: Any) : 
    DomainException("$entityName com ID $id não encontrado")

class BusinessRuleException(message: String) : DomainException(message)

class InvalidEntityException(message: String) : DomainException(message)

class DuplicateEntityException(message: String) : DomainException(message)
