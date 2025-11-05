package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.DashBoardPharmacyResponse
import edu.fatec.petwise.application.dto.InventoryItemDTO
import edu.fatec.petwise.application.dto.PharmacyOrderDTO
import edu.fatec.petwise.application.dto.SaleDTO
import edu.fatec.petwise.application.usecase.ManageInventoryUseCase
import edu.fatec.petwise.application.usecase.ManageSalesUseCase
import edu.fatec.petwise.domain.enums.PaymentMethod
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter
import org.springframework.data.domain.PageRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard da Farmácia", description = "Endpoints para visualização do dashboard da farmácia veterinária")
class DashboardPharmacyController(
    private val manageInventoryUseCase: ManageInventoryUseCase,
    private val manageSalesUseCase: ManageSalesUseCase,
    private val content: OrderedFormContentFilter
) {

    @GetMapping("/status-cards")
    @Operation(
        summary = "Recupera cards de status do dashboard",
        description = "Retorna informações resumidas sobre estoque, vendas e status do inventário"
    )
    fun getStatusCards(): ResponseEntity<DashBoardPharmacyResponse.StatusCardsDTO> {
        try {
            val lowStockItems: List<InventoryItemDTO> = manageInventoryUseCase.getLowStockItems()
            val expiringItems: List<InventoryItemDTO> = manageInventoryUseCase.getExpiringItems(30)

            val today = LocalDateTime.now()
            val startOfDay = today.toLocalDate().atStartOfDay()
            val endOfDay = today.toLocalDate().atTime(23, 59, 59)

            val todaySalesStats = manageSalesUseCase.getSalesStatistics(startOfDay, endOfDay)
            val startOfWeek = today.minusDays(7)
            val weekSalesStats = manageSalesUseCase.getSalesStatistics(startOfWeek, today)

            val recentSales: List<SaleDTO> = manageSalesUseCase.getAllSales(PageRequest.of(0, 10)).content
            val allInventoryItems: List<InventoryItemDTO> =
                manageInventoryUseCase.getAllInventoryItems(PageRequest.of(0, 1000))

            val totalInventoryValue = allInventoryItems.fold(BigDecimal.ZERO) { acc: BigDecimal, item: InventoryItemDTO ->
                acc.add(item.unitPrice?.multiply(BigDecimal.valueOf(item.currentStock.toLong())) ?: BigDecimal.ZERO)
            }

            val activeItemsCount = allInventoryItems.count { item: InventoryItemDTO -> item.isActive }

            val pendingOrdersCount = 0L
            val pendingOrdersAmount = BigDecimal.ZERO
            val pendingOrders = emptyList<PharmacyOrderDTO>()

            val lowStockCard = DashBoardPharmacyResponse.LowStockCard(
                totalItems = lowStockItems.size.toLong(),
                criticalItems = lowStockItems.count { item: InventoryItemDTO -> item.currentStock == 0 }.toLong(),
                items = lowStockItems.take(5)
            )

            val pendingOrdersCard = DashBoardPharmacyResponse.PendingOrdersCard(
                totalPending = pendingOrdersCount,
                totalAmount = pendingOrdersAmount,
                pendingOrders = pendingOrders.take(5)
            )

            val recentSalesCard = DashBoardPharmacyResponse.RecentSalesCard(
                todaySales = todaySalesStats.totalSales,
                todayRevenue = todaySalesStats.totalRevenue,
                weekSales = weekSalesStats.totalSales,
                weekRevenue = weekSalesStats.totalRevenue,
                recentSales = recentSales
            )

            val inventoryStatusCard = DashBoardPharmacyResponse.InventoryStatusCard(
                totalItems = allInventoryItems.size.toLong(),
                activeItems = activeItemsCount.toLong(),
                totalValue = totalInventoryValue,
                expiringItems = expiringItems.size
            )

            val statusCards = DashBoardPharmacyResponse.StatusCardsDTO(
                lowStockItems = lowStockCard,
                pendingOrders = pendingOrdersCard,
                recentSales = recentSalesCard,
                inventoryStatus = inventoryStatusCard
            )

            return ResponseEntity.ok(statusCards)
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao recuperar dados dos cards de status: ${ex.message}", ex)
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Recupera estatísticas do dashboard")
    fun getStatistics(
        @Parameter(description = "Data inicial", example = "2024-01-01")
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") startDate: LocalDate,

        @Parameter(description = "Data final", example = "2024-12-31")
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") endDate: LocalDate
    ): ResponseEntity<DashBoardPharmacyResponse.DashboardStatisticsDTO> {
        try {
            if (startDate.isAfter(endDate)) return ResponseEntity.badRequest().build()

            val startDateTime = startDate.atStartOfDay()
            val endDateTime = endDate.atTime(23, 59, 59)

            val salesStats = manageSalesUseCase.getSalesStatistics(startDateTime, endDateTime)
            val periodDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1
            val previousStartDate = startDate.minusDays(periodDays)
            val previousEndDate = startDate.minusDays(1)

            val previousSalesStats =
                manageSalesUseCase.getSalesStatistics(previousStartDate.atStartOfDay(), previousEndDate.atTime(23, 59, 59))

            val salesGrowth = if (previousSalesStats.totalSales > 0) {
                val growth = (salesStats.totalSales - previousSalesStats.totalSales).toBigDecimal()
                growth.divide(previousSalesStats.totalSales.toBigDecimal(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
            } else BigDecimal.valueOf(100)

            val inventoryItems: List<InventoryItemDTO> = manageInventoryUseCase.getAllInventoryItems(PageRequest.of(0, 1000))

            val inventoryValue = inventoryItems.fold(BigDecimal.ZERO) { acc: BigDecimal, item: InventoryItemDTO ->
                acc.add(item.unitPrice?.multiply(BigDecimal.valueOf(item.currentStock.toLong())) ?: BigDecimal.ZERO)
            }

            val lowStockItemsCount = inventoryItems.count { item: InventoryItemDTO -> item.isLowStock }
            val expiringItemsCount = manageInventoryUseCase.getExpiringItems(30).size

            val averageStockLevel = if (inventoryItems.isNotEmpty()) {
                val avg = inventoryItems.map { item: InventoryItemDTO -> item.currentStock }.average()
                BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
            } else BigDecimal.ZERO

            val monthlyData = generateMonthlyData(startDateTime, endDateTime)
            val paymentMethodData = salesStats.salesByPaymentMethod.entries.map { entry: Map.Entry<PaymentMethod, Long> ->
                val percentage = if (salesStats.totalSales > 0) {
                    entry.value.toBigDecimal()
                        .divide(salesStats.totalSales.toBigDecimal(), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                } else BigDecimal.ZERO
                DashBoardPharmacyResponse.PaymentMethodData(entry.key, entry.value, percentage)
            }

            val topSellingData = generateTopSellingData(startDateTime, endDateTime)

            val inventoryLevelData = inventoryItems.map { item: InventoryItemDTO ->
                DashBoardPharmacyResponse.InventoryLevelData(
                    medicationName = item.medicationName,
                    currentStock = item.currentStock,
                    minimumStock = item.minimumStock,
                    status = item.stockStatus ?: "Normal"
                )
            }.take(10)

            val salesTrend = DashBoardPharmacyResponse.SalesTrend(
                trend = if (salesGrowth > BigDecimal.ZERO) "Crescimento" else if (salesGrowth < BigDecimal.ZERO) "Queda" else "Estável",
                percentage = salesGrowth.abs(),
                direction = if (salesGrowth > BigDecimal.ZERO) "up" else if (salesGrowth < BigDecimal.ZERO) "down" else "stable"
            )

            val inventoryTrend = DashBoardPharmacyResponse.InventoryTrend("Análise em Desenvolvimento", BigDecimal.ZERO, "stable")

            val salesStatistics = DashBoardPharmacyResponse.SalesStatistics(
                totalSales = salesStats.totalSales,
                totalRevenue = salesStats.totalRevenue,
                averageTicket = salesStats.averageTicket,
                salesByPaymentMethod = salesStats.salesByPaymentMethod,
                salesGrowth = salesGrowth,
                previousPeriodSales = previousSalesStats.totalSales
            )

            val inventoryStatistics = DashBoardPharmacyResponse.InventoryStatistics(
                totalItems = inventoryItems.size.toLong(),
                totalValue = inventoryValue,
                lowStockCount = lowStockItemsCount.toLong(),
                expiringCount = expiringItemsCount.toLong(),
                averageStockLevel = averageStockLevel,
                inventoryTurnover = BigDecimal.ZERO
            )

            val chartData = DashBoardPharmacyResponse.ChartData(
                salesByMonth = monthlyData,
                salesByPaymentMethod = paymentMethodData,
                topSellingMedications = topSellingData,
                inventoryLevels = inventoryLevelData
            )

            val trendsData = DashBoardPharmacyResponse.TrendsData(salesTrend, inventoryTrend)

            val topProducts = topSellingData.take(5).map { data: DashBoardPharmacyResponse.MedicationSalesData ->
                DashBoardPharmacyResponse.TopProduct(data.medicationName, data.salesCount, data.totalRevenue, BigDecimal.ZERO)
            }

            val periodInfo = DashBoardPharmacyResponse.PeriodInfo(startDateTime, endDateTime, "$startDate até $endDate")

            val dashboardStatistics = DashBoardPharmacyResponse.DashboardStatisticsDTO(
                salesStats = salesStatistics,
                inventoryStats = inventoryStatistics,
                chartData = chartData,
                trendsData = trendsData,
                topProducts = topProducts,
                periodInfo = periodInfo
            )

            return ResponseEntity.ok(dashboardStatistics)
        } catch (ex: Exception) {
            throw RuntimeException("Erro ao recuperar estatísticas: ${ex.message}", ex)
        }
    }

    private fun generateMonthlyData(startDate: LocalDateTime, endDate: LocalDateTime): List<DashBoardPharmacyResponse.MonthlyData> {
        val monthlyData = mutableListOf<DashBoardPharmacyResponse.MonthlyData>()
        var currentDate = startDate.withDayOfMonth(1)

        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate.withDayOfMonth(1))) {
            val monthEnd = currentDate.plusMonths(1).minusDays(1)
            val actualEndDate = if (monthEnd.isAfter(endDate)) endDate else monthEnd
            val stats = manageSalesUseCase.getSalesStatistics(currentDate, actualEndDate)

            monthlyData.add(
                DashBoardPharmacyResponse.MonthlyData(
                    month = currentDate.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")),
                    sales = stats.totalSales,
                    revenue = stats.totalRevenue,
                    monthNumber = currentDate.monthValue,
                    year = currentDate.year
                )
            )

            currentDate = currentDate.plusMonths(1)
        }

        return monthlyData
    }

    private fun generateTopSellingData(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<DashBoardPharmacyResponse.MedicationSalesData> {
        val sales = manageSalesUseCase.getSalesStatistics(startDate, endDate)

        return sales.topMedications.mapNotNull { medicationName: String ->
            val medicationSales = manageSalesUseCase.searchSalesByMedication(
                medicationName,
                PageRequest.of(0, 1000)
            ).content

            if (medicationSales.isNotEmpty()) {
                val totalQuantity = medicationSales.sumOf { sale: SaleDTO -> sale.quantity }
                val totalRevenue = medicationSales.fold(BigDecimal.ZERO) { acc: BigDecimal, sale: SaleDTO ->
                    acc.add(sale.totalAmount)
                }

                DashBoardPharmacyResponse.MedicationSalesData(
                    medicationName = medicationName,
                    totalQuantity = totalQuantity.toLong(),
                    totalRevenue = totalRevenue,
                    salesCount = medicationSales.size.toLong()
                )
            } else null
        }
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity.BodyBuilder {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Erro interno do servidor",
            "message" to ex.message,
            "path" to "/api/dashboard/*"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
