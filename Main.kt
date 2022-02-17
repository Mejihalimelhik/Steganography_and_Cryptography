package flashcards

import java.io.File
import java.io.FileNotFoundException

var cards = mutableMapOf<String, Pair<String, Int>>()
var logs = mutableListOf<String>()

fun main() {
    while (true) {
        printOutput("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when(readInput()) {
            "add" -> add()
            "remove" -> remove()
            "import" -> import()
            "export" -> export()
            "ask" -> ask()
            "log" -> log()
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
            "exit" -> break
        }
    }
    printOutput("Bye bye!")
}

fun add() {
    printOutput("The card:")
    val term: String = checkExist(::termExistPrint, ::cardContainsKey) ?: return
    printOutput("The definition of the card:")
    val definition: String = checkExist(::definitionExistPrint, ::cardContainsValue) ?: return
    cards[term] = Pair(definition, 0)
    printOutput("The pair (\"$term\":\"$definition\") has been added.")
}

fun remove() {
    printOutput("Which card?")
    val term: String = checkExist(::removeTermExist, ::cardContainsKey) ?: return
    printOutput("Can't remove \"$term\": there is no such card.")
}

fun import() {
    printOutput("File name:")
    val fileName = readInput()
    try {
        val cardsFile = File(fileName)
        val cardsText = cardsFile.readText()
        cards.putAll(textToMap(cardsText))
        printOutput("${textToMap(cardsText).size} cards have been loaded.")
    } catch (e: FileNotFoundException) {
        printOutput("File not found.")
    }
}

fun export() {
    printOutput("File name:")
    val fileName = readInput()
    File(fileName).writeText(mapToText(cards))
    printOutput("${cards.size} cards have been saved.")
}

fun log() {
    printOutput("File name:")
    val fileName = readInput()
    File(fileName).writeText(logs.joinToString("\n"))
    printOutput("The log has been saved.")
}

fun hardestCard() {
    val max = cards.map { it.value.second }.maxOrNull() ?: 0
    val hardestCardsList = cards.filterValues { it.second == max }.map { "\"${it.key }\"" }
    if (max == 0) {
        printOutput("There are no cards with errors.")
    } else if (hardestCardsList.size == 1) {
        printOutput("The hardest card is \"${hardestCardsList.first()}\". You have $max errors answering it")
    } else {
        printOutput("The hardest cards are ${hardestCardsList.joinToString(", ")}. You have n errors answering them")
    }
}

fun resetStats() {
    cards = cards.mapValues { Pair(it.value.first, 0) }.toMutableMap()
    printOutput("Card statistics have been reset.")
}

fun ask() {
    printOutput("How many times to ask?")
    val askNumber =readInput().toInt()
    cards.onEachIndexed { index, entry ->
        if (index == askNumber) return
        printOutput("Print the definition of \"${entry.key}\":")
        readInput().let {
            if (it == entry.value.first) {
                printOutput("Correct!")
            } else {
                cards[entry.key] = Pair(entry.value.first, entry.value.second + 1)
                printOutput(
                    "Wrong. The right answer is \"${entry.value.first}\"${
                        if (cardContainsValue(it)) ", but your definition is correct for \"${getKey(it)}\"." else "."
                    }"
                )
            }
        }
    }
}

fun checkExist(fPerform: (String) -> Unit, fContains: (String) -> Boolean): String? {
    readInput().let {
        return if (fContains(it)) { fPerform(it); null } else it
    }
}

fun mapToText(map: MutableMap<String, Pair<String, Int>>): String {
    var mapToString = ""
    map.forEach { (k, v) -> mapToString = mapToString.plus("$k - ${v.first} : ${v.second}\n")}
    return mapToString.dropLast(1)
}

fun textToMap(text: String): MutableMap<String, Pair<String, Int>> {
    val map = mutableMapOf<String, Pair<String, Int>>()
    text.split("\n").map { it.split(" - ") }.forEach {
        it.last().split(" : ").run {
            map[it.first()] = Pair(first(), last().toInt())
        }
    }
    return map
}

fun readInput(): String{
    val input = readLine()!!.toString()
    logs.add(input)
    return input
}

fun printOutput(output: String) {
    logs.add(output)
    println(output)
}

fun removeTermExist(term: String) {
    cards.remove(term)
    printOutput("The card has been removed.")
}

fun getKey(value: String): String = cards.filterValues { it.first == value }.keys.first()

fun definitionExistPrint(definition: String) = printOutput("The definition \"$definition\" already exists.")

fun termExistPrint(term: String) = printOutput("The card \"$term\" already exists.")

fun cardContainsValue(input: String) = cardsToTermDefinition(cards).containsValue(input)

fun cardContainsKey(input: String) = cards.containsKey(input)

fun cardsToTermDefinition(map: MutableMap<String, Pair<String, Int>>) = map.entries.associate { it.key to it.value.first }