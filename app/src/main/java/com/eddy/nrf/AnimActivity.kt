package com.eddy.nrf

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eddy.nrf.databinding.ActivityAnimBinding

class AnimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimBinding

    private lateinit var anim: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        anim = binding.imageview.background as AnimationDrawable
        anim.start()

        binding.button.setOnClickListener { view ->
            if (anim.isRunning)
                anim.stop()
            else
                anim.start()
        }

    }
}