package ru.bsprogramm.findthecolor.game

interface GameInterface {

    fun startGame()
    fun updateScore(score : Int)
    fun loose(score: Int)
    fun tik(value : Int)

}
