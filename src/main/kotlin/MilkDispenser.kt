class OutOfBoundsContainerException(message:String):Exception(message)
class MilkDispenser(val id:String, val maxBays:Int = 6) {
    private val containerBays: MutableList<LiquidContainer?> = MutableList(maxBays){null}
    @Throws(OutOfBoundsContainerException::class)
    fun addContainer(container: LiquidContainer, bay: Int) {
        if (bay !in 0..<maxBays) throw OutOfBoundsContainerException("Invalid position. Position should be between 0 and ${maxBays - 1}")
        if(containerBays[bay] != null) throw OutOfBoundsContainerException("Invalid position. There is already a container at that location")
        containerBays.add(bay,container)
    }

    @Throws(OutOfBoundsContainerException::class)
    fun removeContainer(bay: Int): LiquidContainer? {
        if(containerBays[bay]== null) throw OutOfBoundsContainerException("Invalid position. No Container at given location")
        return containerBays[bay]
    }

}