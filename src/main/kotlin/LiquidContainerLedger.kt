import java.util.*

data class ContainerLedgerEntry(val date: Date, val amount: Double)

class LiquidContainerLedger {
    private val entries = mutableListOf<ContainerLedgerEntry>()

    fun fill(amount: Double, date: Date) {
        entries.add(ContainerLedgerEntry(date, amount))
    }

    fun drain(amount: Double, date: Date) {
        entries.add(ContainerLedgerEntry(date, -amount))
    }

    fun getBalance(): Double {
        return entries.sumOf { it.amount }
    }

}