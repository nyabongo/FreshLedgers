import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.math.round

class LiquidContainerLedgerTest {
    private val fourDaysAway = Date(Date().time + 4 * 24 * 60 * 60 * 1000)
    private val batch = Batch("TestBatch", 1000_000.0, fourDaysAway)
    private val batchTwo = Batch("TestBatchTwo", 1000_000.0, fourDaysAway)
    private val batches = arrayOf<Batch>(
        Batch("b_000",100_000.0,fourDaysAway),
        Batch("b_001",100_000.0,fourDaysAway),
        Batch("b_002",100_000.0,fourDaysAway),
        Batch("b_003",100_000.0,fourDaysAway),
        Batch("b_004",100_000.0,fourDaysAway)
    )

    private lateinit var ledger: LiquidContainerLedger

    @BeforeEach
    fun setUp() {
        ledger = LiquidContainerLedger()
    }

    @Test
    fun `should be able to add a fill entry to the ledger with an amount batch and  date`() {
        ledger.fill(1000.0, batch, Date())
    }

    @Test
    fun `it should be able to return the batches in the ledger`() {
        assert(ledger.getBatches().isEmpty())
        ledger.fill(1000.0, batch, Date())
        assert(ledger.getBatches().contains(batch))
        ledger.fill(1000.0, batchTwo, Date())
        assert(ledger.getBatches().contains(batchTwo))
    }

    @Test
    fun `should be able to add a drain entry to the ledger after which the balance should be less by the right amount`() {
        val fillAmount = 1000.0
        val drainAmount = 333.0
        ledger.fill(fillAmount, batch, Date())
        ledger.drain(drainAmount, Date())
        assertEquals(fillAmount-drainAmount,ledger.getBalance())
    }

    @Test
    fun `should be able to get the volume of a specific batch and return zero if none of that batch exists`() {
        val batchFillAmount = 1000.0
        val batchTwoFillAmount= 400.0
        assertEquals(0.0,ledger.getBatchVolume(batch))
        ledger.fill(batchFillAmount,batch,Date())
        ledger.fill(batchTwoFillAmount,batchTwo,Date())
        val batchVolume = ledger.getBatchVolume(batch)
        val batchTwoVolume = ledger.getBatchVolume(batchTwo)
        assertEquals(batchTwoVolume,batchTwoFillAmount)
        assertEquals(batchVolume,batchFillAmount)
    }

    @Test
    fun `drain should reduce the amount of each batch in proportion`() {
        val batchFillAmount = 1000.0
        val batchTwoFillAmount= 500.0
        val totalFill = batchFillAmount+batchTwoFillAmount
        val drainAmount = 200.0
        ledger.fill(batchFillAmount,batch,Date())
        ledger.fill(batchTwoFillAmount,batchTwo,Date())
        ledger.drain(drainAmount,Date())
        val expectedBatchVolume = batchFillAmount - (drainAmount*batchFillAmount/totalFill)
        val expectedBatchTwoVolume = batchTwoFillAmount - (drainAmount*batchTwoFillAmount/totalFill)
        val actualBatchVolume = ledger.getBatchVolume(batch)
        val actualBatchTwoVolume = ledger.getBatchVolume(batchTwo)

        assertEquals(expectedBatchVolume, actualBatchVolume , 0.001)
        assertEquals(expectedBatchTwoVolume, actualBatchTwoVolume,0.001)
    }

    @Test
    fun `drain should return a report showing the volume of each batch dispensed`() {
        val batchFillAmount = 1000.0
        val batchTwoFillAmount= 500.0
        val drainAmount = 200.0
        ledger.fill(batchFillAmount,batch,Date())
        ledger.fill(batchTwoFillAmount,batchTwo,Date())
        val drainReport = ledger.drain(drainAmount,Date())
        val drainedBatchVolume = batchFillAmount - ledger.getBatchVolume(batch)
        val drainedBatchTwoVolume = batchTwoFillAmount - ledger.getBatchVolume(batchTwo)
        val expectedReport = mutableMapOf<Batch, Double>()
        expectedReport[batch] = round(drainedBatchVolume*1000)/1000
        expectedReport[batchTwo] = round(drainedBatchTwoVolume*1000)/1000
        assertEquals(expectedReport,drainReport)
    }

    @Test
    fun `sanity check`() {
        assertEquals(0.0, ledger.getBalance())
        ledger.fill(2000.0,batches[1])
        assertEquals(2000.0,ledger.getBatchVolume(batches[1]))
        ledger.drain(500.0)
        assertEquals(1500.0,ledger.getBatchVolume(batches[1]))
        ledger.fill(1000.0,batches[2])
        assertEquals(2500.0,ledger.getBalance())
        ledger.drain(500.0)
        assertEquals(2000.0*0.6,ledger.getBatchVolume(batches[1]))
        assertEquals(2000.0*0.4,ledger.getBatchVolume(batches[2]))
        ledger.fill(2000.0,batches[2])
        assertEquals(4000.0*0.3,ledger.getBatchVolume(batches[1]))
        assertEquals(4000.0*0.7,ledger.getBatchVolume(batches[2]))
        ledger.fill(1000.0,batches[4])
        assertEquals(5000.0*0.24,ledger.getBatchVolume(batches[1]),0.0001)
        assertEquals(5000.0*0.56,ledger.getBatchVolume(batches[2]),0.0001)
        assertEquals(5000.0*0.2,ledger.getBatchVolume(batches[4]),0.0001)
    }
}