import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Date

class LiquidContainerTest {
    private lateinit var batch: Batch
    private lateinit var container: LiquidContainer
    private lateinit var ledger: ContainerLedger

    @BeforeEach
    fun setUp() {
        val fourDaysAway = Date(Date().time + 4 * 24 * 60 * 60 * 1000)
        batch = Batch("TestBatch", 1000_000.0 , fourDaysAway)
        ledger = ContainerLedger()
        container = LiquidContainer(id = "TestContainer", batch, capacity = 24_000.0, ledger)
    }

    @Test
    fun `Should be able to fill the container with a volume`(){
        container.fill(amount=2000.0, date= Date())
    }

    @Test
    fun `Should not be able to be filled beyond its capacity`(){
        assertThrows<ContainerOverfillException> {
            container.fill(container.capacity+1,Date())
        }
    }

    @Test
    fun `should not be able to be filled beyond its capacity even with multiple fills`(){
        container.fill(container.capacity/3, Date())
        container.fill(container.capacity/3, Date())
        assertThrows<ContainerOverfillException> {
            container.fill(3+container.capacity/3, Date())
        }
    }

    @Test
    fun `should be able to drain the container`(){
        container.fill(container.capacity)
        container.drain(container.capacity/2, Date())
    }

    @Test
    fun `should not be able to drain the container below its capacity`(){
        container.fill(container.capacity)
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
        container.fill(container.capacity)
        container.drain(container.capacity/2)
        assertThrows<ContainerOverDrainException> {
            container.drain(1+container.capacity/2)
        }
    }

    @Test
    fun `should be able to return the volume left in the container`() {
        assert(container.balance == 0.0)
        container.fill(container.capacity)
        assert(container.balance == container.capacity)
        container.drain(container.capacity)
        assert(container.balance == 0.0)
        container.fill(container.capacity/2)
        assert(container.balance == container.capacity/2)
        container.drain(container.capacity/3)
        assert(container.balance.equals(container.capacity/6) )
    }
}