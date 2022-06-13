package com.hit.mysurfaceview

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import org.json.*
import java.io.*
import java.net.Socket


class MySurfaceView(context: Context?) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    var window_height: Int=0
    var count = 0
    var screenWidth = 480
    var screenHeight = 800
    var mbLoop = false 
    private val mSurfaceHolder: SurfaceHolder?
    private var canvas 
    : Canvas? =
            null
    private val mPaint: Paint





    val BG= BitmapFactory.decodeResource(context!!.resources,R.drawable.bg)
    val HERO=BitmapFactory.decodeResource(context!!.resources,R.drawable.hero)
    val BULLET=BitmapFactory.decodeResource(context!!.resources,R.drawable.bullet_hero)
    val EBULLET=BitmapFactory.decodeResource(context!!.resources,R.drawable.bullet_enemy)
    val TRIVIAL=BitmapFactory.decodeResource(context!!.resources,R.drawable.mob)
    val ELITE=BitmapFactory.decodeResource(context!!.resources,R.drawable.elite)
    val BOSS=BitmapFactory.decodeResource(context!!.resources,R.drawable.boss)

    val mappping= mapOf<String,Bitmap>(
        "hero_bullets" to BULLET,
        "trivial_enemy" to TRIVIAL,
        "elite_enemy" to ELITE,
        "boss_enemy" to BOSS,
        "enemy_bullets" to EBULLET,
        "heroes" to HERO,
    )


    fun draw(x: Int, y: Int,g:Bitmap) {

        mPaint.isAntiAlias = true
        mPaint.color = Color.GREEN

        canvas!!.drawBitmap(g,x.toFloat()-g.width/2, y.toFloat()-g.height/2, mPaint)

    }


    fun str2loc(s: String): List<Int> {
        val l = s.split(",").map { s -> s.toInt() }
        return l
    }
    var x = 50.0
    var y = 50.0

    var room_id=-1


    var ready=false

    fun showAlertWithTextInputLayout(context: Context) {
        val textInputLayout = TextInputLayout(context)

        val input = EditText(context)
        textInputLayout.hint = "114514"
        textInputLayout.addView(input)

        val alert = AlertDialog.Builder(context)
            .setTitle("Join a Game")
            .setView(textInputLayout)
            .setMessage("Please enter room ID")
            .setPositiveButton("Go") { dialog, _ ->
                // do some thing with input.text
                room_id=input.text.toString().toIntOrNull()?:114514
                dialog.cancel()
            }.create()

        alert.show()
    }

    fun gameStart(context: Context) {
        val textInputLayout = TextInputLayout(context)



        val alert = AlertDialog.Builder(context)
            .setTitle("Waiting for your teammate")
            .setView(textInputLayout)
            //.setMessage("Please enter room ID")
            .setPositiveButton("Go") { dialog, _ ->
                // do some thing with input.text
                dialog.cancel()
                ready=true
            }.create()

        alert.show()
    }


    override fun run() {









        val serverName = "192.168.1.129"
        val port = 11451
        var img_top=0f

        var game_over=false

        while(true){

            room_id=-1

            val handler = Handler(Looper.getMainLooper())

            handler.post {
                showAlertWithTextInputLayout(context)
            }



            while (room_id==-1){
            Thread.sleep(100)
            }




        game_over=false
        try {
                println("connecting to：$serverName ，port：$port")
                val client = Socket(serverName, port)
                val print_out: PrintStream =  PrintStream(client.getOutputStream())

                val inFromServer = client.getInputStream()
                val buf = BufferedReader(InputStreamReader(inFromServer))
                print_out.println(room_id)



                ready=false

                if(buf.readLine()=="host") {
                    handler.post {
                        gameStart(context)
                    }

                    while (!ready) {
                        Thread.sleep(200)
                    }

                    print_out.println("-1")
                    print_out.flush()
                }


                while (!game_over) {
                    if (buf.ready()) {

                        canvas = mSurfaceHolder!!.lockCanvas()
                        if (mSurfaceHolder == null || canvas == null) {
                            return
                        }

                        canvas!!.drawBitmap(BG,0f,img_top,mPaint)
                        canvas!!.drawBitmap(BG,0f,img_top-screenHeight+8,mPaint)
                        if(img_top<screenHeight){
                            img_top+=1
                        }else {
                            img_top = 0f
                        }
                        mPaint.color = Color.BLUE

                        val s = buf.readLine()
                        val jsonObj = JSONObject(s)


                        try {

                            for (k in mappping.keys){
                                val j=jsonObj.getJSONArray(k)
                                drawByJSONArray(j,mappping.getOrDefault(k,BULLET))
                            }
                        }catch(e:Exception){
                            //do nothing
                            e.printStackTrace()
                        }
                        //draw(heroloc[0], heroloc[1],HERO)
                        mSurfaceHolder.unlockCanvasAndPost(canvas)


                        print_out.println("%d,%d".format(x.toInt(),y.toInt()))
                        print_out.flush()

                        game_over=jsonObj.getJSONArray("heroes").length()==0

                    }

            }
                print_out.close()
                buf.close()
            println("game over")
        } catch (e: IOException) {
            e.printStackTrace()
        }
            room_id=-1
        }

    }



    fun drawByJSONArray(l:JSONArray,d:Bitmap){
        for (i in 0..l!!.length() - 1) {
            val loc = l.getString(i)
            val loc_ = str2loc(loc)
            draw(loc_[0], loc_[1],d)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Thread(this).start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mbLoop = false
    }

    init {
        mbLoop = true
        mPaint = Paint() 
        mSurfaceHolder = this.holder
        mSurfaceHolder.addCallback(this)
        this.isFocusable = true
    }


}
