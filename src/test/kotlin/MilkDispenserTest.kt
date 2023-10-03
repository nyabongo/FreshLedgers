import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.util.*

class MilkDispenserTest {
    private val tomorrow = Date(Date().time + 1 * 24 * 60 * 60 * 1000)
    private val batch = Batch("defaultBatch", 200_000.0, tomorrow )
    private val dispenserId = "dispenser_ID"
    private val defaultContainerBays= 6
    private lateinit var container: LiquidContainer
    private lateinit var dispenser: MilkDispenser
    @BeforeEach
    fun setUp() {
        container = LiquidContainer("container", batch, 20_000.0,ContainerLedger())
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
    fun `should be able to remove containers`() {
        dispenser.addContainer(container, bay=2)
        val removedContainer:LiquidContainer? = dispenser.removeContainer(bay=2)
        assertEquals(container,removedContainer)
    }

}