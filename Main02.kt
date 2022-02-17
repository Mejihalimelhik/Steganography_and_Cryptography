
fun isPrime(n: Int, i: Int = 2): Boolean = when {
    n == i -> true
    n % i == 0 -> false
    else -> {
        isPrime(n, i + 1)
    }
}

fun main() = println(isPrime(readLine()!!.toInt()))