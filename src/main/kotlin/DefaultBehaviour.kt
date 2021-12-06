import jade.core.AID
import jade.core.behaviours.SimpleBehaviour
import jade.lang.acl.ACLMessage
import kotlin.random.Random

class DefaultBehaviour (private val agent: AverageAgent, private var number: Double, private val peers: List<AID>): SimpleBehaviour() {
    private var state = State.ACTIVE
    private var prevNumber = number
    private val alpha = 1/3

    override fun action() {
        when (state) {
            State.ACTIVE -> {
                sendAverageToPeers()

                val ts = getAveragesFromPeers()
                if (!updateAverage(ts)) {
                    state = State.PRINT
                }
            }
            State.PRINT -> {
                printResultIfCan()
                sendAverageToPeers()
                state = State.DONE
            }
            else -> {}
        }
    }

    override fun done(): Boolean {
        log(state.toString())
        return state == State.DONE
    }

    private fun getAveragesFromPeers(): List<Double> {
        log("get tables from peers")
        val numbers = mutableListOf<Double>()
        val neededPeers = peers.map { it.name }.toMutableSet()

        while (neededPeers.isNotEmpty()) {
            log("request table from ${neededPeers.size} peers")

            val msg = agent.blockingReceive()
            if (msg != null) {
                neededPeers.remove(msg.sender.name)
                numbers.add(msg.content.toDouble())
            } else {
                println("AGENT ${agent.name} received NULL")
            }
        }

        return numbers
    }

    private fun sendAverageToPeers() {
        if (Random.nextDouble() < 0.05) { return }
        log("send table to peers")
        val msg = ACLMessage(ACLMessage.INFORM)
        msg.content = if (Random.nextDouble() > 0.03) number.toString() else prevNumber.toString()
        peers.forEach(msg::addReceiver)

        agent.send(msg)
    }

    private fun updateAverage(numbers: List<Double>): Boolean {
        log("update number")

        prevNumber = number
        number = alpha * (number + numbers.sum())

        return true
    }

    private fun printResultIfCan() {
        log("print result if can")
        log("debug: $number")

        if (agent.name == AID("1st", false).name) {
            log("AVERAGE: $number")
        }
    }

    private enum class State {
        ACTIVE,
        PRINT,
        DONE
    }

    private fun log(msg: String) = println("${agent.name}: $msg")
}