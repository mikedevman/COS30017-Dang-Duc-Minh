package com.example.week08

import java.io.File

fun main() {
    File("C:\\Users\\Admin\\AndroidStudioProjects\\COS30017-Dang-Duc-Minh\\Week08\\app\\src\\main\\java\\com\\example\\week08\\input.txt")
        .forEachLine { println(it) }

    val list = mutableListOf<Word>()
        File("C:\\Users\\Admin\\AndroidStudioProjects\\COS30017-Dang-Duc-Minh\\Week08\\app\\src\\main\\java\\com\\example\\week08\\input.txt")
//        .readLines()
//            .forEachLine{list.add(Word(it))}
            .forEachLine{
                val temp = it.split(",")
                list.add(Word(temp[0], temp[1].toInt()))
            }

    for (line in list) {
        println(line)
        println(line.word)
    }

    list.forEach { println(it.word) }
    list.forEach {"${it.word} -- ${it.num}"}
}

data class Word(val word: String, val num: Int)