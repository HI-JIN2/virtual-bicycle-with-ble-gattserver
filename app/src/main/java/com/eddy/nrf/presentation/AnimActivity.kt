package com.eddy.nrf.presentation

import android.content.pm.ActivityInfo
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.eddy.nrf.databinding.ActivityAnimBinding


class AnimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimBinding

    private lateinit var anim: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(binding.root)

        anim = binding.imageview.background as AnimationDrawable
        anim.start()

//        binding.button.setOnClickListener { view ->
//            if (anim.isRunning) {
//                anim.stop()
//            } else {
//                anim.start()
//            }
//        }
//
//        binding.buttonFast.setOnClickListener { view ->
//            adjustAnimationSpeed(binding.imageview, anim, 25) // 각 프레임의 지속 시간을 100ms로 설정
//        }
//        binding.buttonSlow.setOnClickListener { view ->
//            adjustAnimationSpeed(binding.imageview, anim, 1000) // 각 프레임의 지속 시간을 100ms로 설정
//        }


    }

    private fun adjustAnimationSpeed(
        imageView: ImageView,
        animationDrawable: AnimationDrawable,
        frameDuration: Long
    ) {
        val handler: Handler = Handler()
        val frameCount = animationDrawable.numberOfFrames

        val updateAnimation: Runnable = object : Runnable {
            private var currentFrame = 0

            override fun run() {
                if (currentFrame < frameCount) {
                    imageView.setImageDrawable(animationDrawable)
                    animationDrawable.selectDrawable(currentFrame)
                    currentFrame = (currentFrame + 1) % frameCount
                    handler.postDelayed(this, frameDuration)
                }
            }
        }

        handler.post(updateAnimation)
    }
}