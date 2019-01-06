package ru.bsprogramm.findthecolor.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.TableLayout
import kotlinx.android.synthetic.main.activity_game.*
import ru.bsprogramm.findthecolor.R
import ru.bsprogramm.findthecolor.game.Game
import ru.bsprogramm.findthecolor.game.GameInterface
import ru.bsprogramm.findthecolor.makeFullScreen


class GameActivity : AppCompatActivity(), GameInterface {


    override fun startGame() {

    }

    override fun updateScore(scoreI: Int) {
        this.score.post {
            score.text = "$scoreI"
        }
    }

    override fun tik(value: Int) {
        time.post { time.text = "$value" }
    }

    override fun loose(score: Int) {
        if (alert == null) { // Защита от быстрых нажатий пользователя
            alert = makeRestartDialog(score)
            alert!!.show()
        }
    }

    var game: Game? = null
    var alert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()

        setContentView(R.layout.activity_game)

        val field = findViewById<TableLayout>(R.id.grid)
        game = Game(field, this)
        game?.startGame(false)
    }

    /**
     * Генерирует диалог с игровой информацией
     * @param score - счет в игре
     * @return Возвращает объект диалогового окна с нужными игровыми данными
     */
    fun makeRestartDialog(score: Int): AlertDialog {
        val builder = AlertDialog.Builder(this@GameActivity)
        builder.setTitle("Проигрыш")
            .setMessage("Вы проиграли! \n Ваш счет: $score \n Попробуйте еще раз, у вас обязательно получится лучше)")
            .setCancelable(false)
            .setNegativeButton(
                "Еще раз"
            ) { _, _ ->
                run {
                    game?.startGame(true)
                    alert = null
                }
            }
        return builder.create()
    }
}
