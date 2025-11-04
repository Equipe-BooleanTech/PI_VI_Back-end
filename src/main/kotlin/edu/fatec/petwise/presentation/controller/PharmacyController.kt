package edu.fatec.petwise.presentation.controller


import edu.fatec.petwise.application.dto.InventoryItemDTO
import edu.fatec.petwise.application.dto.PharmacyOrderDTO
import edu.fatec.petwise.application.dto.SaleDTO
import edu.fatec.petwise.application.dto.SupplierDTO
import edu.fatec.petwise.application.usecase.ManageInventoryUseCase
import edu.fatec.petwise.application.usecase.ManageOrdersUseCase
import edu.fatec.petwise.application.usecase.ManageSalesUseCase
import edu.fatec.petwise.application.usecase.ManageSuppliersUseCase
import edu.fatec.petwise.domain.enums.OrderStatus
import edu.fatec.petwise.domain.enums.PaymentMethod
import edu.fatec.petwise.domain.enums.PharmacyOrderStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/pharmacy")
@Tag(name = "Pharmacy", description = "Endpoints para gerenciamento da farmácia veterinária")
class PharmacyController(
    private val manageInventoryUseCase: ManageInventoryUseCase,
    private val manageOrdersUseCase: ManageOrdersUseCase,
    private val manageSalesUseCase: ManageSalesUseCase,
    private val manageSuppliersUseCase: ManageSuppliersUseCase
) {

    @GetMapping("/inventory")
    @Operation(
        summary = "Listar inventário de medicamentos",
        description = "Retorna lista paginada de todos os medicamentos no inventário"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Lista de medicamentos retornada com sucesso"),
        ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    )
    fun getAllInventoryItems(
        @Parameter(description = "Configurações de paginação")
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<List<InventoryItemDTO>> {
        return ResponseEntity.ok(manageInventoryUseCase.getAllInventoryItems(pageable))
    }

    @PostMapping("/inventory")
    @Operation(
        summary = "Adicionar item ao inventário",
        description = "Adiciona um novo medicamento ao inventário da farmácia"
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados inválidos"),
        ApiResponse(responseCode = "409", description = "Conflito - fornecedor não encontrado")
    )
    fun createInventoryItem(
        @Parameter(description = "Dados do medicamento a ser adicionado")
        @Valid @RequestBody dto: InventoryItemDTO
    ): ResponseEntity<InventoryItemDTO> {
        val created = manageInventoryUseCase.createInventoryItem(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping("/inventory/{id}")
    @Operation(
        summary = "Obter detalhes do item",
        description = "Retorna detalhes completos de um medicamento específico do inventário"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Item encontrado"),
        ApiResponse(responseCode = "404", description = "Item não encontrado")
    )
    fun getInventoryItem(
        @Parameter(description = "ID do medicamento", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<InventoryItemDTO> {
        return manageInventoryUseCase.getInventoryItem(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/inventory/{id}")
    @Operation(
        summary = "Atualizar item do inventário",
        description = "Atualiza informações de um medicamento existente"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
        ApiResponse(responseCode = "404", description = "Item não encontrado"),
        ApiResponse(responseCode = "400", description = "Dados inválidos")
    )
    fun updateInventoryItem(
        @Parameter(description = "ID do medicamento", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Dados atualizados do medicamento")
        @Valid @RequestBody dto: InventoryItemDTO
    ): ResponseEntity<InventoryItemDTO> {
        return try {
            val updated = manageInventoryUseCase.updateInventoryItem(id, dto)
            ResponseEntity.ok(updated)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/inventory/{id}")
    @Operation(
        summary = "Remover item do inventário",
        description = "Remove um medicamento do inventário (marca como inativo)"
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
        ApiResponse(responseCode = "404", description = "Item não encontrado")
    )
    fun deleteInventoryItem(
        @Parameter(description = "ID do medicamento", required = true)
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        return if (manageInventoryUseCase.deleteInventoryItem(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/orders")
    @Operation(
        summary = "Listar pedidos",
        description = "Retorna lista paginada de todos os pedidos de medicamentos"
    )
    fun getAllOrders(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<PharmacyOrderDTO>> {
        return ResponseEntity.ok(manageOrdersUseCase.getAllOrders(pageable))
    }

    @PostMapping("/orders")
    @Operation(
        summary = "Criar pedido",
        description = "Cria um novo pedido de medicamentos junto ao fornecedor"
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados do pedido inválidos")
    )
    fun createOrder(
        @Valid @RequestBody dto: PharmacyOrderDTO
    ): ResponseEntity<PharmacyOrderDTO> {
        val created = manageOrdersUseCase.createOrder(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping("/orders/{id}")
    @Operation(
        summary = "Obter detalhes do pedido",
        description = "Retorna detalhes completos de um pedido específico"
    )
    fun getOrder(
        @PathVariable id: UUID
    ): Any {
        val order = manageOrdersUseCase.getOrder(id)
        return order?.let { ResponseEntity.ok() } ?: ResponseEntity.notFound()
    }

    @PutMapping("/orders/{id}/status")
    @Operation(
        summary = "Atualizar status do pedido",
        description = "Atualiza o status de um pedido existente"
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        ApiResponse(responseCode = "400", description = "Transição de status inválida")
    )
    fun updateOrderStatus(
        @PathVariable id: UUID,
        @RequestParam status: PharmacyOrderStatus
    ): ResponseEntity<PharmacyOrderDTO> {
        return try {
            val updated = manageOrdersUseCase.updateOrderStatus(id, status)
            ResponseEntity.ok(updated)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/sales")
    @Operation(
        summary = "Histórico de vendas",
        description = "Retorna histórico de vendas de medicamentos com filtros opcionais"
    )
    fun getSalesHistory(
        @RequestParam(required = false) startDate: java.time.LocalDateTime?,
        @RequestParam(required = false) endDate: java.time.LocalDateTime?,
        @RequestParam(required = false) paymentMethod: PaymentMethod?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<SaleDTO>> {
        val sales = when {
            startDate != null && endDate != null -> {
                manageSalesUseCase.getSalesByDateRange(startDate, endDate, pageable)
            }
            paymentMethod != null -> {
                manageSalesUseCase.getSalesByPaymentMethod(paymentMethod, pageable)
            }
            else -> {
                manageSalesUseCase.getAllSales(pageable)
            }
        }
        return ResponseEntity.ok(sales)
    }

    @PostMapping("/sales")
    @Operation(
        summary = "Registrar venda",
        description = "Registra uma nova venda de medicamento"
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Venda registrada com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados da venda inválidos"),
        ApiResponse(responseCode = "409", description = "Estoque insuficiente")
    )
    fun registerSale(
        @Valid @RequestBody dto: SaleDTO
    ): ResponseEntity<SaleDTO> {
        val sale = manageSalesUseCase.processSaleWithInventoryUpdate(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(sale)
    }

    @GetMapping("/suppliers")
    @Operation(
        summary = "Listar fornecedores",
        description = "Retorna lista paginada de todos os fornecedores cadastrados"
    )
    fun getAllSuppliers(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<SupplierDTO>> {
        return ResponseEntity.ok(manageSuppliersUseCase.getAllSuppliers(pageable))
    }

    @PostMapping("/suppliers")
    @Operation(
        summary = "Adicionar fornecedor",
        description = "Adiciona um novo fornecedor de medicamentos"
    )
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Fornecedor adicionado com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados do fornecedor inválidos"),
        ApiResponse(responseCode = "409", description = "CNPJ/CPF já cadastrado")
    )
    fun addSupplier(
        @Valid @RequestBody dto: SupplierDTO
    ): ResponseEntity<SupplierDTO> {
        val created = manageSuppliersUseCase.createSupplier(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }
}