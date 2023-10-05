import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Date

class LiquidContainerTest {
    private val fourDaysAway = Date(Date().time + 4 * 24 * 60 * 60 * 1000)
    private lateinit var batch: Batch
    private val batchTwo = Batch("TestBatchTwo", 1000_000.0, fourDaysAway)
    private lateinit var container: LiquidContainer
    private lateinit var ledger: LiquidContainerLedger

    @BeforeEach
    fun setUp() {
        batch = Batch("TestBatch", 1000_000.0 , fourDaysAway)
        ledger = LiquidContainerLedger()
        container = LiquidContainer(id = "TestContainer",  capacity = 24_000.0, ledger)
    }

    @Test
    fun `Should be able to fill the container with a volume`(){
        container.fill(amount=2000.0,batch, date= Date())
    }

    @Test
    fun `Should not be able to be filled beyond its capacity`(){
        assertThrows<ContainerOverfillException> {
            container.fill(container.capacity+1,batch, Date())
        }
    }

    @Test
    fun `should not be able to be filled beyond its capacity even with multiple fills`(){
        container.fill(container.capacity/3, batch)
        container.fill(container.capacity/3, batch)
        assertThrows<ContainerOverfillException> {
            container.fill(3+container.capacity/3, batch)
        }
    }

    @Test
    fun `should be able to drain the container`(){
        container.fill(container.capacity, batch)
        container.drain(container.capacity/2, Date())
    }

    @Test
    fun `should not be able to drain the container below its capacity`(){
        container.fill(container.capacity, batch)
        assertThrows<ContainerOverDrainException> {
            container.drain(container.capacity+1)
        }
    }

    @Test
    fun `should not be able to be drained beyond its available volume`() {
        assert(container.balance == 0.0)
        assertThrows<ContainerOverDrainException> {
            container.drain(20.0)
        }
        container.fill(container.capacity, batch)
        container.drain(container.capacity/2)
        assertThrows<ContainerOverDrainException> {
            container.drain(1+container.capacity/2)
        }
    }

    @Test
    fun `should be able to return the volume left in the container`() {
        assert(container.balance == 0.0)
        container.fill(container.capacity, batch)
        assert(container.balance == container.capacity)
        container.drain(container.capacity)
        assert(container.balance == 0.0)
        container.fill(container.capacity/2, batch)
        assert(container.balance == container.capacity/2)
        container.drain(container.capacity/3)
        assert(container.balance.equals(container.capacity/6) )
    }


    @Test
    fun `it should be able to return the batches in the ledger`() {
        assert(container.getBatches().isEmpty())
        container.fill(1000.0, batch, Date())
        assert(container.getBatches().contains(batch))
        container.fill(1000.0, batchTwo, Date())
        assert(container.getBatches().contains(batchTwo))
    }
    @Test
    fun `should be able to get the volume of a specific batch and return zero if none of that batch exists`() {
        val batchFillAmount = 1000.0
        val batchTwoFillAmount= 400.0
        Assertions.assertEquals(0.0, container.getBatchVolume(batch))
        container.fill(batchFillAmount,batch,Date())
        container.fill(batchTwoFillAmount,batchTwo,Date())
        val batchVolume = container.getBatchVolume(batch)
        val batchTwoVolume = container.getBatchVolume(batchTwo)
        Assertions.assertEquals(batchTwoVolume, batchTwoFillAmount)
        Assertions.assertEquals(batchVolume, batchFillAmount)
    }
}