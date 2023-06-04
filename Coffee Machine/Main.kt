package machine

object Machine {
    private var waterAvailable = 400
    private var milkAvailable = 540
    private var beansAvailable = 120
    private var emptyCupsAvailable = 9
    private var moneyAvailable = 550

    fun printState() {
        println("The coffee machine has: ")
        println("$waterAvailable ml of water")
        println("$milkAvailable ml of milk")
        println("$beansAvailable mg of coffee beans")
        println("$emptyCupsAvailable disposable cups")
        println("$$moneyAvailable of money")
    }

    fun sellCoffee() {
        println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino: ")
        when (readln().toInt()) {
            1 -> makeCoffee(250, 0, 16, 4)
            2 -> makeCoffee(350, 75, 20, 7)
            3 -> makeCoffee(200, 100, 12, 6)
        }
    }

    private fun makeCoffee(water: Int, milk: Int, beans: Int, cost: Int) {
        waterAvailable -= water
        milkAvailable -= milk
        beansAvailable -= beans
        emptyCupsAvailable -= 1
        moneyAvailable += cost
    }

    fun fillMachine() {
        println("Write how many ml of water you want to add: ")
        waterAvailable += readln().toInt()

        println("Write how many ml of milk you want to add: ")
        milkAvailable += readln().toInt()

        println("Write how many grams of coffee you want to add: ")
        beansAvailable += readln().toInt()

        println("Write how many disposable cups you want to add: ")
        emptyCupsAvailable += readln().toInt()
    }

    fun giveMoney() {
        println("I gave you $$moneyAvailable")
        moneyAvailable = 0
    }
}

fun main() {
    println("Write action (buy, fill, take): ")
    when (readln()) {
        "buy" -> Machine.sellCoffee()
        "fill" -> Machine.fillMachine()
        "take" -> Machine.giveMoney()
        "remaining" -> Machine.printState()
        "exit" -> return
    }
}