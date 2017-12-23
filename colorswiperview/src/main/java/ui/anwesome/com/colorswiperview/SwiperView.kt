package ui.anwesome.com.colorswiperview

/**
 * Created by anweshmishra on 23/12/17.
 */
import android.content.*
import android.graphics.*
import android.view.*
import java.util.concurrent.ConcurrentLinkedQueue

val colors:Array<String> = arrayOf("#f44336","#9C27B0","#009688","#BF360C","#C51162")
class SwiperView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        return true
    }
    data class Screen(var w:Float,var h:Float = 0f,var x:Float = 0f,var prevX:Float = 0f,var j:Float = 0f){
        val state = State()
        fun update(updateDb:(Int)->Unit) {
            val updateFn:(Float,Float,Int)->Unit = { scale,dirf,dir ->
                x = prevX + dir*scale*w
                if(dirf == 0f) {
                    x = prevX+dir*w
                    prevX = x
                    j+=dir
                }
            }
            state.update()
            state.executeFn(updateFn)
        }
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,0f)
            canvas.restore()
        }
        fun startUpdating(dir:Int) {
            state.startUpdating(dir)
        }
    }
    data class State(var dir:Int = 0,var scale:Float = 0f,var dirf:Float = 0f) {
        fun update() {
            scale += dirf*0.1f
            if(scale > 1) {
                scale = 0f
                dirf = 0f
            }
        }
        fun startUpdating(dir:Int) {
            dirf = 1f
            this.dir = dir
        }
        fun executeFn(cb:(Float,Float,Int)->Unit) {
            cb(scale,dirf,dir)
        }
    }
    data class ColorBoxScreen(var i:Int) {
        fun drawOnScreen(canvas: Canvas,paint:Paint,w:Float,h:Float) {
            val x = w*i
            paint.color = Color.parseColor(colors[i])
            canvas.drawRect(RectF(x,0f,x+w,h),paint)
        }
        fun drawOnLeft(canvas: Canvas,paint:Paint,w:Float,h:Float,size:Float) {
            paint.color = Color.parseColor(colors[i])
            canvas.drawRoundRect(RectF(w-size,h-size,w,h),size/10,size/10,paint)
        }
        fun drawOnRight(canvas: Canvas,paint:Paint,w:Float,h:Float,size:Float) {
            paint.color = Color.parseColor(colors[i])
            canvas.drawRoundRect(RectF(0f,h-size,size,h),size/10,size/10,paint)
        }
    }
}
fun ConcurrentLinkedQueue<SwiperView.ColorBoxScreen>.getAt(i:Int):SwiperView.ColorBoxScreen? {
    var index = 0
    this.forEach { it ->
        if(i == index) {
            return it
        }
        index++
    }
    return null
}