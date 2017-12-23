package ui.anwesome.com.colorswiperview

/**
 * Created by anweshmishra on 23/12/17.
 */
import android.content.*
import android.graphics.*
import android.view.*
class SwiperView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        return true
    }
}