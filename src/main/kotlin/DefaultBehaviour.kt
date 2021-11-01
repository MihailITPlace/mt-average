import jade.core.AID
import jade.core.behaviours.SimpleBehaviour
import jade.lang.acl.ACLMessage

class DefaultBehaviour constructor(private val agent: AverageAgent, number: Int, private val peers: List<AID>): SimpleBehaviour() {
    private var state = State.ACTIVE
    private val table = hashMapOf<String, Int>(agent.name to number)

    override fun action() {
        when (state) {
            State.ACTIVE -> {
                sendTableToPeers()

                val ts = getTablesFromPeers()
                if (!updateTable(ts)) {
                    state = State.PRINT
                }
            }
            State.PRINT -> {
                printResultIfCan()
                sendTableToPeers()
                state = State.DONE
            }
            else -> {}
        }
    }

    override fun done(): Boolean {
        log(state.toString())
        return state == State.DONE
    }

    private fun getTablesFromPeers(): List<Map<String, Int>> {
        log("get tables from peers")
        val result = mutableListOf<Map<String, Int>>()
        val neededPeers = peers.map { it.name }.toMutableSet()
        while (neededPeers.isNotEmpty()) {
            log("request table from ${neededPeers.size} peers")

            val msg = agent.blockingReceive()
            if (msg != null) {
                neededPeers.remove(msg.sender.name)
                result.add(msg.content.toTable())
            } else {
                println("AGENT ${agent.name} received NULL")
            }
        }

        return result
    }

    private fun sendTableToPeers() {
        log("send table to peers")
        val msg = ACLMessage(ACLMessage.INFORM)
        msg.content = table.toMessage()
        peers.forEach(msg::addReceiver)

        agent.send(msg)
    }

    private fun updateTable(ts: List<Map<String, Int>>): Boolean {
        log("update table")
        if (ts.isNotEmpty() && ts.all { it.keys == table.keys }) {
            return false
        }

        for (t in ts) {
            for (i in t) {
                table.putIfAbsent(i.key, i.value)
            }
        }

        return true
    }

    private fun printResultIfCan() {
        log("print result if can")
        log("debug: ${table.toMessage()}")
        if (agent.name == table.keys.minOf { it }) {
            val average = table.values.sum().toDouble() / table.size
            log("AVERAGE: $average")
        }
    }

    private enum class State {
        ACTIVE,
        PRINT,
        DONE
    }

    private fun String.toTable(): Map<String, Int> {
        return split(";").associate {
            val t = it.split(",")
            t[0] to t[1].toInt()
        }
    }

    private fun Map<String, Int>.toMessage(): String {
        return this.entries.joinToString(";") { "${it.key},${it.value}" }
    }

    private fun log(msg: String) = println("${agent.name}: $msg")
}