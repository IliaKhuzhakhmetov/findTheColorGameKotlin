package ru.bsprogramm.findthecolor.game

import android.graphics.Color
import android.os.CountDownTimer
import android.support.v7.widget.CardView
import android.util.Log
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import java.util.*


/**
 * Класс игры отвечающий за предоставления уровней и счета,
 * а также все что связанно с игровым процессом
 * @param
 */
class Game(val field: TableLayout, val listener: GameInterface) {

    private val TIME_FOR_LEVEL = 10_000

    private val TAG = "Game"
    private var levelNow = 0
    private var score = 0

    private var timer = timerForLevel(0, 0)
    private var timerTime = 0

    // Уровни
    private val levels = arrayListOf(
        Level(2, 105),
        //3
        Level(3, 75),
        Level(3, 60),
        //4
        Level(4, 45),
        Level(4, 30),
        Level(4, 20),
        Level(4, 18),
        //5
        Level(5, 16),
        Level(5, 15),
        Level(5, 15),
        Level(5, 15),
        //6
        Level(6, 14),
        Level(6, 14),
        Level(6, 14),
        Level(6, 13),
        Level(6, 13),
        Level(6, 13),
        Level(6, 12),
        Level(6, 12),
        Level(6, 12),
        //7
        Level(7, 11),
        Level(7, 11),
        Level(7, 11),
        Level(7, 10),
        Level(7, 10),
        Level(7, 9),
        Level(7, 9),
        Level(7, 8),
        Level(7, 8),
        Level(7, 7),
        //8
        Level(8, 7),
        Level(8, 7),
        Level(8, 7),
        Level(8, 6),
        //9
        Level(9, 6),
        Level(9, 6),
        Level(9, 6),
        Level(9, 5),
        //10
        Level(10, 5),
        Level(10, 5),
        Level(10, 5),
        Level(10, 4),
        //11
        Level(11, 4),
        Level(11, 4),
        Level(11, 4),
        Level(11, 3),
        Level(11, 3),
        //12
        Level(12, 2),
        Level(12, 1)
    )

    /**
     * Функция возвращающая случайный цвет, в зависимости от уровня
     * @param level - уровень для которого нужен цвет
     */
    fun getRandomColor(level: Level): ColorRGB {
        val colorDiff = level.colorDiff
        var r = Random().nextInt(255 - colorDiff) + colorDiff
        var g = Random().nextInt(255 - colorDiff) + colorDiff
        var b = Random().nextInt(255 - colorDiff) + colorDiff
        if (255 <= r + colorDiff) r = 255 - colorDiff
        if (255 <= g + colorDiff) g = 255 - colorDiff
        if (255 <= b + colorDiff) b = 255 - colorDiff
        return ColorRGB(r, g, b)
    }

    /**
     * Получение актуального уровня
     * @return актуальный уровень
     */
    fun getCurrLevel() = levels[levelNow]

    /**
     * Функция чистит игровое полее от ранее созданых на ней объектов
     */
    private fun clearField() {
        field.removeAllViews()
    }

    /**
     * Функция отрисовывающая уровень на игровом поле
     * @param level уровень, который нужно отрисовать
     */
    private fun createLevel(level: Level) {

        val gridSize = level.sizeGrid
        val colorDiff = level.colorDiff
        val context = field.context
        val randomColor = getRandomColor(level)

        val randI = Random().nextInt(gridSize)
        val randJ = Random().nextInt(gridSize)
        for (i in 0 until gridSize) {
            val tableRow = TableRow(context)
            tableRow.layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.MATCH_PARENT,
                1f
            )
            for (j in 0 until gridSize) {
                val cardView = CardView(context)
                val params =
                    TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1f
                    )
                params.setMargins(2, 2, 2, 2)
                cardView.layoutParams = params
                if (i == randI && j == randJ) {
                    cardView.setOnClickListener {
                        score++
                        nextLevel()
                    }
                    cardView.setCardBackgroundColor(
                        Color.rgb(
                            randomColor.r + colorDiff,
                            randomColor.g + colorDiff,
                            randomColor.b + colorDiff
                        )
                    )
                } else {
                    cardView.setOnClickListener {
                        val time = timerTime - 2
                        if (time > 0 ) {
                            clearTimers()
                            timer = timerForLevel(time * 1_000.toLong(), 1_000)
                            timer.start()
                        } else listener.loose(score)
                    }
                    cardView.setCardBackgroundColor(Color.rgb(randomColor.r, randomColor.g, randomColor.b))
                }
                tableRow.addView(cardView, j)
            }

            field.addView(tableRow, i)
        }
    }

    /**
     * Функция перехода на следующий уровень
     */
    private fun nextLevel() {
        Log.d(TAG, "Загрузка следующего уровня")
        levelNow++
        clearField()
        createLevel(getCurrLevel())
        restartTimers()
        listener.updateScore(score)
    }


    /**
     * Перезапуск игрового таймера
     */
    private fun restartTimers(){
        clearTimers()
        timer = timerForLevel(TIME_FOR_LEVEL.toLong(), 1_000)
        timer.start()
    }

    /**
     * Очистка таймеров игры
     */
    private fun clearTimers() {
        timer.cancel()
        timerTime = 0
    }

    /**
     * Старт игры
     */
    fun startGame(startTimer : Boolean) {
        // Обнуляем уровень и счет
        levelNow = 0
        score = 0

        listener.updateScore(score)
        listener.tik(10)

        createLevel(level = levels[levelNow])
        if (!startTimer) clearTimers()
    }

    /**
     * Функция возвращает объект класса Timer с заданными параметрами
     * Объект предназначен для контролирования времени отведенное игроку на прохождение уровня
     * @param millisInFuture - Сколько секунд идёт на обратный отсчет
     * @param countDownInterval - С каким интервалом будет идти отсчет
     */
    private fun timerForLevel(millisInFuture: Long, countDownInterval: Long): CountDownTimer {
        return object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                listener.tik(millisUntilFinished.toInt() / 1000)
                timerTime = millisUntilFinished.toInt() / 1000
            }

            override fun onFinish() {
                listener.loose(score)
                clearField()
            }
        }
    }

    /**
     * Вспомогательный класс для цвета
     */
    class ColorRGB(val r: Int, val g: Int, val b: Int)

    /**
     * Класс уровня
     * @param sizeGrid - Размер сетки у уровня
     * @param colorDiff - Отличие цвета от основного
     */
    class Level(val sizeGrid: Int, val colorDiff: Int)

}
