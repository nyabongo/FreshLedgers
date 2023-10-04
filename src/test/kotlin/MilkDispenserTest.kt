import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.util.*

class MilkDispenserTest {
    private val tomorrow = Date(Date().time + 1 * 24 * 60 * 60 * 1000)
    private val batch = Batch("defaultBatch", 200_000.0, tomorrow )
    private val batch_two = Batch("batch_two", 200_000.0, tomorrow )
    private val dispenserId = "dispenser_ID"
    private val defaultContainerBays= 6
    private lateinit var container: LiquidContainer
    private lateinit var container_two: LiquidContainer
    private lateinit var container_three: LiquidContainer
    private lateinit var container_four: LiquidContainer
    private lateinit var dispenser: MilkDispenser
    @BeforeEach
    fun setUp() {
        container = LiquidContainer("container", batch, 20_000.0,ContainerLedger())
        container.fill(container.capacity)
        container_two=LiquidContainer("c2",batch, 20_000.0,)
        container_two.fill(container_two.capacity)
        container_three=LiquidContainer("c3",batch_two, 20_000.0,)
        container_three.fill(container_three.capacity)
        container_four=LiquidContainer("c4",batch_two, 20_000.0,)
        container_four.fill(container_four.capacity)
        dispenser = MilkDispenser(dispenserId)
    }

    @Test
    fun getId() {
        assertEquals(dispenser.id , dispenserId)
    }

    @Test
    fun getMaxContainerBays() {
        assertEquals(dispenser.maxBays , defaultContainerBays)
    }

    @Test
    fun `should be able to add a container`() {
        dispenser.addContainer(container, bay=0)
    }

    @Test
    fun `should not be able to add a container to a bay that doesn't exist`() {
        val otherDispenser = MilkDispenser("new_Dispenser", 2)
        assertThrows<OutOfBoundsContainerException>{
            otherDispenser.addContainer(container, otherDispenser.maxBays)
        }
    }

    @Test
    fun `should not be able to add a container to a bay with a container`() {
        dispenser.addContainer(container, bay=3)
        assertThrows<OutOfBoundsContainerException>("Invalid position. There is already a container at that location"){
            dispenser.addContainer(container, bay=3)
        }
    }

    @Test
    fun `should not be able to remove a container from abay that doesn't have one`() {
        assertThrows<OutOfBoundsContainerException>("Invalid position. No Container at given location"){
            dispenser.removeContainer(bay=2)
        }
    }

    @Test
    fun `should throw an error if a request is made to remove a container from beyond the existing bays`() {
        assertThrows<OutOfBoundsContainerException>(
            "Invalid position. Position should be between 0 and ${dispenser.maxBays - 1}") {
            dispenser.removeContainer(dispenser.maxBays)
        }
    }

    @Test
    fun `should be able to remove containers`() {
        dispenser.addContainer(container, bay=2)
        val removedContainer:LiquidContainer? = dispenser.removeContainer(bay=2)
        assertEquals(container,removedContainer)
    }

    @Test
    fun `it should return null if an attempt is made to retrieve a batch from an empty or none existent bay`() {
        assertNull(dispenser.getBatchAtBay(1))
        assertNull(dispenser.getBatchAtBay(dispenser.maxBays))
    }

    @Test
    fun `should be able to return the Batch of the milk in a container in a dispenser bay`() {
        dispenser.addContainer(container, 3)
        assertEquals(dispenser.getBatchAtBay(3), container.batch)
    }

    @Test
    fun `it should return a volume of zero if there is no container at the location or a wrong location is specified`() {
        val bay =4
        assertNull(dispenser.getBatchAtBay(bay))
        assertEquals(dispenser.getContainerVolume(bay), 0.0)
    }

    @Test
    fun `should return the volume contained in container at a specified location`() {
        val initialContainerVolume = container.balance
        dispenser.addContainer(container,4)
        assertEquals(dispenser.getContainerVolume(4),initialContainerVolume)
        val dispenseVolume = 1_000.0
        container.drain(dispenseVolume)
        assertEquals(dispenser.getContainerVolume(4),initialContainerVolume-dispenseVolume)
    }

}