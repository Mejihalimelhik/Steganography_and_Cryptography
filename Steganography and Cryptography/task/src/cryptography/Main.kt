import java.io.File
import javax.imageio.ImageIO

/**
 * Just a comment to push in git
 */

fun main() {
    while(true) {
        println("Task (hide, show, exit):")
        readLine().let {
            when(it) {
                "exit" -> { println("Bye!"); return }
                "hide" -> { println("Hiding message in image."); hide() }
                "show" -> { println("Input image file:"); show() }
                else -> println("Wrong task: $it")
            }
        }
    }
}

fun show() = mutableListOf<Int>().run {
    ImageIO.read(File(readLine()!!)).run {
        for (y in 0 until height) { for (x in 0 until width) { add(getRGB(x, y) and 1) } }
        val s = encryptDecrypt(joinToString("").substringBefore("000000000000000000000011"))
        println("Message:\n${java.math.BigInteger(s, 2).toByteArray().toString(Charsets.UTF_8)}")
    }
}

fun encryptDecrypt(s: String): String {
    var encrypted = ""
    println("Password:")
    val pW = readLine()!!.map { Integer.toBinaryString(it.code).padStart(8, '0') }.joinToString("")
    s.forEachIndexed { index, c -> encrypted = encrypted.plus(c.code xor pW[index % pW.length].code) }
    return encrypted
}

fun hide() {
    println("Input image file:")
    val inputFile =  File(readLine()!!)
    println("Output image file:")
    val outputFile = File(readLine()!!)
    println("Message to hide:")
    val msgToHide = readLine()!!.map { Integer.toBinaryString(it.code).padStart(8, '0') }.joinToString("")
    val msg = encryptDecrypt(msgToHide).plus("000000000000000000000011")
    try {
        ImageIO.read(inputFile).run {
            if (msg.length > width * height) throw java.lang.Exception()
            println("Input Image: ${inputFile.absolutePath}")
            println("Output Image: ${outputFile.absolutePath}")
            msg.forEachIndexed { index, c ->
                setRGB(
                    index % width,
                    index / width,
                    getRGB(index % width, index / width) and 0xFFFFFE or c.toString().toInt()
                )
            }
            ImageIO.write(this, "PNG", outputFile)
        }
        println("Message saved in ${outputFile.absolutePath} image.")
    } catch (e: javax.imageio.IIOException) {
        println("Can't read input file!")
    } catch (e: java.lang.Exception) {
        println("The input image is not large enough to hold this message.")
    }
}
