package com.hit.mysurfaceview

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import me.caibou.rockerview.JoystickView


class MainActivity : AppCompatActivity() {
    private var mMySurfaceView: MySurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mMySurfaceView = MySurfaceView(this)


        //val fm=

        setContentView(mMySurfaceView)


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mMySurfaceView!!.x = event.x.toDouble()
            mMySurfaceView!!.y = event.y.toDouble()

        }

        return true
    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return true
    }
}

