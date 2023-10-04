import java.util.*
data class ContainerLedgerEntry(val date: Date, val amount: Double)

class ContainerLedger {
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
class ContainerOverfillException(message: String) : Exception(message)

class ContainerOverDrainException(message: String):Exception(message)

data class LiquidContainer(val id:String,  val capacity:Double, val ledger: ContainerLedger = ContainerLedger()) {

    val balance:Double get() = ledger.getBalance()
    fun fill(amount: Double, batch: Batch, date: Date = Date()) {
        if(amount>capacity){
            throw ContainerOverfillException("Amount Exceeds Container Capacity!")
        }
        if(amount + balance >capacity){
            throw ContainerOverfillException("Amount would exceed Container Capacity")
        }
        ledger.fill(amount,date)
    }

    fun drain(amount: Double, date: Date = Date()) {
        if(amount>capacity){
            throw ContainerOverDrainException("Amount Exceeds Container Capacity!")
        }
        if(amount > balance){
            throw ContainerOverDrainException("Amount Exceeds Container Balance!")
        }
        ledger.drain(amount,date)
    }

}
