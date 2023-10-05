import java.util.*
import kotlin.math.round

data class ContainerLedgerEntry(val date: Date, val amount: Double,val batch: Batch)

class LiquidContainerLedger {
    private val entries = mutableListOf<ContainerLedgerEntry>()

    fun fill(amount: Double,batch: Batch, date: Date = Date()) {
        entries.add(ContainerLedgerEntry(date, amount,batch))
    }

    fun drain(amount: Double, date: Date = Date()): MutableMap<Batch, Double> {
        val totalVolume = getBalance()
        val drainReport = mutableMapOf<Batch, Double>()
        for (batch in getBatches()){
            val batchBalance = getBatchVolume(batch)
            val ratio = batchBalance/totalVolume
            val drainedAmount = round(amount * ratio*1000)/1000
            drainReport[batch]=drainedAmount
            entries.add(ContainerLedgerEntry(date, -drainedAmount, batch))
        }
        return drainReport
    }

    fun getBalance(): Double {
        return entries.sumOf { it.amount }
    }

    fun getBatches(): List<Batch> {
        return entries.map { it.batch }.distinct()
    }

    fun getBatchVolume(batch: Batch): Double {
        return entries.filter { it.batch ==batch }.sumOf { it.amount }
    }

}