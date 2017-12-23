package ui.anwesome.com.colorswiperview

/**
 * Created by anweshmishra on 23/12/17.
 */
import android.app.Activity
import android.content.*
import android.graphics.*
import android.view.*
import java.util.concurrent.ConcurrentLinkedQueue

val colors:Array<String> = arrayOf("#f44336","#9C27B0","#009688","#BF360C","#C51162")
class SwiperView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = ColorScreenRenderer(this)
    val gestureHandler = GestureDetector(ctx,SwipeDetector(renderer))
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas,paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        return gestureHandler.onTouchEvent(event)
    }
    data class Screen(var w:Float,var h:Float = 0f,var x:Float = 0f,var prevX:Float = 0f,var j:Int = 0){
        val colorBoxes:ConcurrentLinkedQueue<ColorBoxScreen> = ConcurrentLinkedQueue()
        val state = State()
        init {
            for(i in 0..colors.size -1) {
                colorBoxes.add(ColorBoxScreen(i))
            }
        }
        fun update(updateDb:(Int)->Unit) {
            val updateFn:(Float,Float,Int)->Unit = { scale,dirf,dir ->
                x = prevX - dir*scale*w
                if(dirf == 0f) {
                    x = prevX-dir*w
                    prevX = x
                    updateDb(j)
                    j+=dir
                }
            }
            state.update()
            state.executeFn(updateFn)
        }
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,0f)
            colorBoxes.forEach {
                it.drawOnScreen(canvas,paint,w,h-w/10)
            }
            canvas.restore()
            if(j > 0) {
                var ls = 0f
                if(state.dir == -1) {
                    ls = state.scale
                }
                colorBoxes.getAt(j-1)?.drawBar(canvas,paint,w,h,w/10,0f,ls)
            }
            if(j < colors.size) {
                var rs = 0f
                if(state.dir == 1) {
                    rs = state.scale
                }
                colorBoxes.getAt(j+1)?.drawBar(canvas,paint,w,h,w/10,w-w/10,rs)
            }
        }
        fun startUpdating(dir:Int) {
            state.startUpdating(dir)
        }
        fun handleTap(x:Float,y:Float,startcb:()->Unit):Boolean {
            val conditions:Array<()->Boolean> = arrayOf({j>0},{j< colors.size-1})
            for(i in 0..1) {
                if (conditions[i].invoke() && handleTapOnBar(x, y, (w-w/10)*(i))) {
                    startUpdating(i*2-1)
                    startcb()
                    return true
                }
            }
            return false
        }
        fun handleTapOnBar(x:Float,y:Float,a:Float):Boolean = x>=a && x<=a+w/10 && y>=h-w/10 && y<=h
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
        fun drawBar(canvas: Canvas,paint:Paint,w:Float,h:Float,size:Float,x:Float,scale:Float) {
            val y = h-size
            paint.color = Color.parseColor(colors[i])
            canvas.drawRoundRect(RectF(x,y,x+size,y+size),size/10,size/10,paint)
            canvas.save()
            paint.color = Color.parseColor("#88EEEEEE")
            canvas.translate(x+size/2,y+size/2)
            canvas.scale(scale,scale)
            canvas.drawRoundRect(RectF(-size/2,-size/2,size/2,size/2),size/10,size/10,paint)
            canvas.restore()
        }
    }
    data class ColorScreenAnimator(var screen:Screen,var view:SwiperView) {
        var animated = false
        fun update() {
            if(animated) {
                screen.update { j ->
                    animated = false
                }
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex:Exception) {

                }
            }
        }
        fun setSwipeDir(dir:Int) {
            if(!animated && ((dir == 1 && screen.j < colors.size-1) || (dir == -1 && screen.j > 0))) {
                screen.startUpdating(dir)
                animated = true
                view.postInvalidate()
            }
        }
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            screen.draw(canvas,paint)
        }
        fun startUpdating(x:Float,y:Float):Boolean {
            if(!animated) {
                return screen.handleTap(x,y,{
                    animated = true
                    view.postInvalidate()
                })
            }
            return false
        }
    }
    data class ColorScreenRenderer(var view:SwiperView,var time:Int = 0) {
        var animator:ColorScreenAnimator?=null
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                animator = ColorScreenAnimator(Screen(w,h),view)
            }
            animator?.draw(canvas,paint)
            animator?.update()
            time++
        }
        fun setSwipeDir(dir:Int) {
            animator?.setSwipeDir(dir)
        }
        fun handleTap(x:Float,y:Float):Boolean {
            return animator?.startUpdating(x,y)?:false
        }
    }
    companion object {
        fun create(activity:Activity):SwiperView {
            val view = SwiperView(activity)
            activity.setContentView(view)
            return view
        }
    }
     data class SwipeDetector(var renderer:ColorScreenRenderer):GestureDetector.SimpleOnGestureListener() {
         var dir = 0
         override fun onDown(event: MotionEvent):Boolean {
             dir = 1
             if(renderer.handleTap(event.x,event.y)) {
                 dir = 0
             }
             return true
         }
         override fun onSingleTapUp(event: MotionEvent):Boolean {
             return true
         }

         override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
             if(Math.abs(velocityX) > Math.abs(velocityY) && dir == 1) {
                 if(velocityX>0) {
                     renderer.setSwipeDir(-1)
                 }
                 else {
                     renderer.setSwipeDir(1)
                 }
             }
             return true
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