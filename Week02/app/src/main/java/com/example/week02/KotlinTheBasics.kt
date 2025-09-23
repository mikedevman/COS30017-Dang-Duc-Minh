package com.example.week02

// Task: Learn about operators and types
// Step 1
fun Math1() {
    val a = 1 + 1
    println(a)
}

fun Math2() {
    val b = 53 - 3
    println(b)
}

fun Math3() {
    val c = 50 / 10
    println(c)
}

fun Math4() {
    val d = 1.0 / 2.0
    println(d)
}

fun Math5() {
    val e = 2.0 * 3.5
    println(e)
}

fun MathA() {
    val f = 7.5 / 2.0
    println(f)
}

fun MathB() {
    val g = 2.2 + 1.0
    println(g)
}

fun MathC(){
    val h = 100.0 / 10.5
    println(h)
}

fun MathA1(){
    val i = 2.times(3)
    println(i)
}

fun MathA2(){
    val j = 3.5.plus(4)
    println(j)
}

fun MathA3(){
    val k = 2.4.div(2)
    println(k)
}

// Step 2
fun Types(){
    val l : Int = 6
    val b1 = l.toByte()
    println(b1)

    val b2: Byte = 1
    println(b2)
    val i4: Int = b2.toInt()
    println(i4)
    val i5: String = b2.toString()
    println(i5)
    val i6: Double = b2.toDouble()
    println(i6)

    val oneMillion = 1_000_000
    val socialSecurityNumber = 999_99_9999L
    val hexBytes = 0xFF_EC_DE_5E
    val bytes = 0b11010010_01101001_10010100_10010010
    println(oneMillion)
    println(socialSecurityNumber)
    println(hexBytes)
    println(bytes)
}

// Step 3
fun VarTypes(){
    var fish: Int = 12
    var lakes: Double = 2.5
    println(fish)
    println(lakes)
}

// Step 4
fun Strings(){
    val numberOfFish = 5
    val numberOfPlants = 12
    println("I have $numberOfFish fish" + " and $numberOfPlants plants")
    println("I have ${numberOfFish + numberOfPlants} fish and plants")
}

// Task: Compare conditions and booleans
fun Booleans(){
    val numberOfFish = 50
    val numberOfPlants = 23
    if (numberOfFish > numberOfPlants) {
        println("Good ratio!")
    } else {
        println("Unhealthy ratio")
    }

    val fish = 50
    if (fish in 1..100) {
        println(fish)
    }

    if (numberOfFish == 0) {
        println("Empty tank")
    } else if (numberOfFish < 40) {
        println("Got fish!")
    } else {
        println("That's a lot of fish!")
    }

    when (numberOfFish) {
        0  -> println("Empty tank")
        in 1..39 -> println("Got fish!")
        else -> println("That's a lot of fish!")
    }
}

// Task: Learn about nullability
// Step 1
fun Null(){
    var rocks: Int? = null
    var marbles: Int? = null
    println(rocks)
    println(marbles)
}

// Step 2
fun NullTest(){
    var fishfoodTreats = 6
    fishfoodTreats = fishfoodTreats?.dec() ?: 0
    println(fishfoodTreats)

    var s: String? = "Hi!"
    var len = s!!.length
    println(len)
}

// Task: Explore arrays, lists, and loops
// Step 1
fun Lists(){
    val school = listOf("mackerel", "trout", "halibut")
    println(school)

    val myList = mutableListOf("tuna", "salmon", "shark")
    myList.remove("shark")
    println(myList)
}

// Step 2
fun Arrays(){
    val school = arrayOf("shark", "salmon", "minnow")
    println(java.util.Arrays.toString(school))

    val mix = arrayOf("fish", 2)
    println(mix)

    val numbers = intArrayOf(1,2,3)
    val numbers3 = intArrayOf(4,5,6)
    val foo2 = numbers3 + numbers
    println(foo2[5])
    val oceans = listOf("Atlantic", "Pacific")
    val oddList = listOf(numbers, oceans, "salmon")
    println(oddList)

    val array = Array (5) { it * 2 }
    println(java.util.Arrays.toString(array))
}

// Step 3
fun Loops(){
    val school = arrayOf("shark", "salmon", "minnow")
    for (element in school) {
        print(element + " ")
    }
    for ((index, element) in school.withIndex()) {
        println("Item at $index is $element\n")
    }

    for (i in 1..5) print(i)
    println()

    for (i in 5 downTo 1) print(i)
    println()

    for (i in 3..6 step 2) print(i)
    println()

    for (i in 'd'..'g') print(i)
    println()

    var bubbles = 0
    while (bubbles < 50) {
        bubbles++
    }
    println("$bubbles bubbles in the water\n")

    do {
        bubbles--
    } while (bubbles > 50)
    println("$bubbles bubbles in the water\n")

    repeat(2) {
        println("A fish is swimming")
    }
}

fun main() {
    print("Task: Learn about operators and types\n")
    print("Step 1:\n")
    Math1()
    Math2()
    Math3()
    Math4()
    Math5()
    MathA()
    MathB()
    MathC()
    MathA1()
    MathA2()
    MathA3()
    print("Step 2:\n")
    Types()
    print("Step 3:\n")
    VarTypes()
    print("Step 4: \n")
    Strings()
    print("Task: Compare conditions and booleans\n")
    Booleans()
    print("Task: Learn about nullability\n")
    print("Step 1:\n")
    Null()
    print("Step 2:\n")
    NullTest()
    print("Task: Explore arrays, lists, and loops\n")
    print("Step 1:\n")
    Lists()
    print("Step 2:\n")
    Arrays()
    print("Step 3:\n")
    Loops()

}
