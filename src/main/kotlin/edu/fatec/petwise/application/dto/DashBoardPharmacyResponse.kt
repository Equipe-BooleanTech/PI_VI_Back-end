package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.PaymentMethod
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import org.springframework.format.annotation.DateTimeFormat
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class DashBoardPharmacyResponse {


    data class StatusCardsDTO(
        val lowStockItems: LowStockCard,
        val pendingOrders: PendingOrdersCard,
        val recentSales: RecentSalesCard,
        val inventoryStatus: InventoryStatusCard
    )

    data class LowStockCard(
        val totalItems: Long,
        val criticalItems: Long,
        val items: List<InventoryItemDTO>,
        val lastUpdated: LocalDateTime = LocalDateTime.now()
    )

    data class PendingOrdersCard(
        val totalPending: Long,
        val totalAmount: BigDecimal,
        val pendingOrders: List<PharmacyOrderDTO>,
        val lastUpdated: LocalDateTime = LocalDateTime.now()
    )

    data class RecentSalesCard(
        val todaySales: Long,
        val todayRevenue: BigDecimal,
        val weekSales: Long,
        val weekRevenue: BigDecimal,
        val recentSales: List<SaleDTO>,
        val lastUpdated: LocalDateTime = LocalDateTime.now()
    )


    data class InventoryStatusCard(
        val totalItems: Long,
        val activeItems: Long,
        val totalValue: BigDecimal,
        val expiringItems: Int,
        val lastUpdated: LocalDateTime = LocalDateTime.now()
    )

    data class DashboardStatisticsDTO(
        val salesStats: SalesStatistics,
        val inventoryStats: InventoryStatistics,
        val chartData: ChartData,
        val trendsData: TrendsData,
        val topProducts: List<TopProduct>,
        val periodInfo: PeriodInfo
    )

    data class SalesStatistics(
        val totalSales: Long,
        val totalRevenue: BigDecimal,
        val averageTicket: BigDecimal,
        val salesByPaymentMethod: Map<PaymentMethod, Long>,
        val salesGrowth: BigDecimal,
        val previousPeriodSales: Long
    )

    data class InventoryStatistics(
        val totalItems: Long,
        val totalValue: BigDecimal,
        val lowStockCount: Long,
        val expiringCount: Long,
        val averageStockLevel: BigDecimal,
        val inventoryTurnover: BigDecimal
    )

    data class ChartData(
        val salesByMonth: List<MonthlyData>,
        val salesByPaymentMethod: List<PaymentMethodData>,
        val topSellingMedications: List<MedicationSalesData>,
        val inventoryLevels: List<InventoryLevelData>
    )

    data class MonthlyData(
        val month: String,
        val sales: Long,
        val revenue: BigDecimal,
        val monthNumber: Int,
        val year: Int
    )

    data class PaymentMethodData(
        val method: PaymentMethod,
        val sales: Long,
        val percentage: BigDecimal
    )


    data class MedicationSalesData(
        val medicationName: String,
        val totalQuantity: Long,
        val totalRevenue: BigDecimal,
        val salesCount: Long
    )


    data class InventoryLevelData(
        val medicationName: String,
        val currentStock: Int,
        val minimumStock: Int,
        val status: String
    )

    data class TrendsData(
        val salesTrend: SalesTrend,
        val inventoryTrend: InventoryTrend
    )

    data class SalesTrend(
        val trend: String,
        val percentage: BigDecimal,
        val direction: String
    )

    data class InventoryTrend(
        val trend: String,
        val percentage: BigDecimal,
        val direction: String
    )


    data class TopProduct(
        val medicationName: String,
        val sales: Long,
        val revenue: BigDecimal,
        val growth: BigDecimal
    )

    data class PeriodInfo(
        val startDate: LocalDateTime,
        val endDate: LocalDateTime,
        val period: String
    )

    data class StatisticsRequest(
        @field:NotNull(message = "Data de início é obrigatória")
        @field:PastOrPresent(message = "Data de início deve ser presente ou passada")
        @field:DateTimeFormat(pattern = "yyyy-MM-dd")
        val startDate: LocalDate,

        @field:NotNull(message = "Data de fim é obrigatória")
        @field:DateTimeFormat(pattern = "yyyy-MM-dd")
        val endDate: LocalDate
    )

}