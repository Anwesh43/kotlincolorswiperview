package ui.anwesome.com.kotlincolorswiperview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.colorswiperview.SwiperView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SwiperView.create(this)
    }
}
