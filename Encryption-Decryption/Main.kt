package encryptdecrypt

import java.io.File

const val ALPHABET = "abcdefghijklmnopqrstuvwxyz"
const val ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun main(args: Array <String>) {
    var operation = "enc"
    var key = 0
    var sentence = ""
    var algorithm = "shift"
    var inFilePath = ""
    var outFilePath = ""

    for (i in args.indices) {
        if (args[i] == "-mode") {
            operation = args[i + 1]
        } else if (args[i] == "-key") {
            key = args[i + 1].toInt()
        } else if (args[i] == "-data") {
            sentence = args[i + 1]
        } else if (args[i] == "-in") {
            inFilePath = args[i + 1]
        } else if (args[i] == "-out") {
            outFilePath = args[i + 1]
        } else if (args[i] == "-alg") {
            algorithm = args[i + 1]
        }
    }

    encryptDecrypt(algorithm, sentence, key, inFilePath, outFilePath, operation)

}

private fun encryptDecrypt(
    algorithm: String,
    sentence: String,
    key: Int,
    inFilePath: String,
    outFilePath: String,
    operation: String
) {
    var newSentence = ""

    if (inFilePath != "" && sentence == "") {
        val file = File(inFilePath)
        val sentenceFromFile = file.readText()

        if (algorithm == "unicode") {
            newSentence = unicodeAlg(sentenceFromFile, key, operation)
        } else if (algorithm == "shift") {
            newSentence = shiftAlg(sentenceFromFile, key, operation)
        }
    } else {
        if (algorithm == "unicode") {
            newSentence = unicodeAlg(sentence, key, operation)
        } else if (algorithm == "shift") {
            newSentence = shiftAlg(sentence, key, operation)
        }
    }

    if (outFilePath != "") {
        File(outFilePath).writeText(newSentence)
        return
    }

    println(newSentence)
}

private fun shiftAlg(
    sentence: String,
    key: Int,
    operation: String
): String {
    var newSentence = ""

    if (operation == "enc") {
        for (symbol in sentence) {
            when (symbol) {
                in ALPHABET -> {
                    var newSymbolIndex = ALPHABET.indexOf(symbol) + key
                    if (newSymbolIndex >= 26) newSymbolIndex -= 26
                    newSentence += ALPHABET[newSymbolIndex]
                }
                in ALPHABET_UPPER -> {
                    var newSymbolIndex = ALPHABET_UPPER.indexOf(symbol) + key
                    if (newSymbolIndex >= 26) newSymbolIndex -= 26
                    newSentence += ALPHABET_UPPER[newSymbolIndex]
                }
                else -> {
                    newSentence += symbol
                }
            }
        }
    } else if (operation == "dec") {
        for (symbol in sentence) {
            when (symbol) {
                in ALPHABET -> {
                    var newSymbolIndex = ALPHABET.indexOf(symbol) - key
                    if (newSymbolIndex < 0) newSymbolIndex += 26
                    newSentence += ALPHABET[newSymbolIndex]
                }
                in ALPHABET_UPPER -> {
                    var newSymbolIndex = ALPHABET_UPPER.indexOf(symbol) - key
                    if (newSymbolIndex < 0) newSymbolIndex += 26
                    newSentence += ALPHABET_UPPER[newSymbolIndex]
                }
                else -> {
                    newSentence += symbol
                }
            }
        }
    }
    return newSentence
}

private fun unicodeAlg(
    sentence: String,
    key: Int,
    operation: String
) : String {
    var newSentence = ""

    if (operation == "enc") {
        for (symbol in sentence) {
            var newSymbolCode = 0
            newSymbolCode = symbol.code + key
            newSentence += newSymbolCode.toChar()
        }
    } else if (operation == "dec") {
        for (symbol in sentence) {
            var newSymbolCode = 0
            newSymbolCode = symbol.code - key
            newSentence += newSymbolCode.toChar()
        }
    }
    return newSentence
}