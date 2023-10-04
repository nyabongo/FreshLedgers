import java.util.*

class ContainerOverfillException(message: String) : Exception(message)

class ContainerOverDrainException(message: String):Exception(message)

data class LiquidContainer(val id:String,  val capacity:Double, val ledger: LiquidContainerLedger = LiquidContainerLedger()) {

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
