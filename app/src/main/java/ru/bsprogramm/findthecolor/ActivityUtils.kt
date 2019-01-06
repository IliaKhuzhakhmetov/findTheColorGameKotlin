package ru.bsprogramm.findthecolor

import android.app.Activity
import android.view.Window
import android.view.WindowManager

/**
 * Вспомогательная функция для применения полноэкранного режима у активности
 */
fun Activity.makeFullScreen(){
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)
}
