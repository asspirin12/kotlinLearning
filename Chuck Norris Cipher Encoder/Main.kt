package chucknorris

fun main() {
    startProgram()
}

private fun startProgram() {
    println("Please input operation (encode/decode/exit):")
    when (val operation = readln()) {
        "encode" -> {
            println("Input string:")
            val input = readln()
            encode(input)
            startProgram()
        }
        "decode" -> {
            println("Input encoded string:")
            val input = readln()
            decode(input)
            startProgram()
        }
        "exit" -> {
            println("Bye!")
            return
        }
        else -> {
            println("There is no '$operation' operation")
            startProgram()
        }
    }
}

private fun decode(zeros: String) {
    val binaryString = zerosToBinary(zeros)

    if (binaryString == "Encoded string is not valid.") {
        println(binaryString)
        return
    }

    var characterString = ""

    for (i in binaryString.indices step 7) {
        val byte = binaryString.substring(i, i + 7)
        characterString += byte.toInt(2).toChar()
    }
    println("Decoded string: ")
    println(characterString)
}

private fun zerosToBinary(zeros: String): String {
    var bytesString = ""

    if (zeros.any { !(it == '0' || it == ' ') }) {
        return "Encoded string is not valid."
    }

    val arrayOfZeros = zeros.split(" ")

    if (arrayOfZeros.size % 2 != 0) {
        return "Encoded string is not valid."
    } else if (!(arrayOfZeros[0] == "0" || arrayOfZeros[0] == "00")) {
        return "Encoded string is not valid."
    }

    for (i in arrayOfZeros.indices step 2) {
        if (arrayOfZeros[i] == "0") {
            bytesString += "1".repeat(arrayOfZeros[i + 1].length)
        } else if (arrayOfZeros[i] == "00") {
            bytesString += "0".repeat(arrayOfZeros[i + 1].length)
        }
    }

    if (bytesString.length % 7 != 0) {
        return "Encoded string is not valid."
    }

    return bytesString
}

private fun encode(sentence: String) {
    val binaryString = lettersToBinary(sentence)

    var chuckNorrisString = ""
    var onesCounter = 0
    var zerosCounter = 0

    for (smb in binaryString) {
        if (smb == '1') {
            if (zerosCounter != 0) {
                chuckNorrisString += "00 " + "0".repeat(zerosCounter) + " "
                zerosCounter = 0
            }
            onesCounter++
        } else if (smb == '0') {
            if (onesCounter != 0) {
                chuckNorrisString += "0 " + "0".repeat(onesCounter) + " "
                onesCounter = 0
            }
            zerosCounter++
        }
    }

    if (onesCounter != 0) {
        chuckNorrisString += "0 " + "0".repeat(onesCounter)
    } else if(zerosCounter != 0) {
        chuckNorrisString += "00 " + "0".repeat(zerosCounter)
    }
    println("Encoded string: ")
    println(chuckNorrisString)
}

private fun lettersToBinary(sentence: String): String {
    var binaryStringAll = ""
    for (letter in sentence) {
        val binaryString = Integer.toBinaryString(letter.code)
        binaryStringAll += String.format("%07d", binaryString.toInt())
    }
    return binaryStringAll
}