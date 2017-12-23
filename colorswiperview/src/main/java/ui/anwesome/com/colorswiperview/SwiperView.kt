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
    data class Screen(var w:Float,var h:Float = 0f,var x:Float = 0f,var prevX:Float = 0f,var j:Float = 0f){
        fun update(updateDb:(Int)->Unit) {
            val updateFn:(Float,Float,Int)->Unit = { scale,dirf,dir ->
                x = prevX + dir*scale*w
                if(dirf == 0f) {
                    x = prevX+dir*w
                    prevX = x
                    j+=dir
                }
            }
        }
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,0f)
            canvas.restore()
        }
        fun startUpdating(dir:Int) {

        }
    }
}