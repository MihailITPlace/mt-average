import jade.core.AID
import jade.core.Agent

class AverageAgent: Agent() {
    override fun setup() {
        println(name)

        if (arguments != null && arguments.isNotEmpty()) {
            val number = arguments[0].toString().toDouble()
            val peers = arguments
                .slice(1 until arguments.size)
                .map { AID(it.toString(), false) }

            addBehaviour(DefaultBehaviour(this, number, peers))
        } else {
            println("${name}: arguments is null or empty :(")
        }
    }
}