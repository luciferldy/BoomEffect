package io.liandy.boomeffect

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.widget.Button
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val LOG_TAG: String = MainActivity::javaClass.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val explosion: ExplosionField = findViewById(R.id.explosion)
//        explosion.explode()

        val start: Button = findViewById(R.id.start)
        start.setOnClickListener {
            if (speed.progress == 0 || gravity.progress == 0) {
                Toast.makeText(baseContext, "set GravitY or Speed first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            explosion.setSpeed(speed.progress)
            explosion.setGravity(gravity.progress / 100.0f)
            explosion.explode()
        }


        val gravityTip: AppCompatTextView = findViewById(R.id.gravity_tip)
        val speedTip: AppCompatTextView = findViewById(R.id.speed_tip)


        val gravity: AppCompatSeekBar = findViewById(R.id.gravity)
        val speed: AppCompatSeekBar = findViewById(R.id.speed)

        speed.max = 50
        speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                speedTip.setText("value = $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        gravity.max = 100
        gravity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value: Float = progress / 100.0f
                gravityTip.setText("value = $value")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        val wind: SwitchCompat = findViewById(R.id.wind)
        wind.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                when(buttonView?.id) {
                    R.id.wind -> {
                        explosion.setWind(isChecked)
                        Log.d(LOG_TAG, "isChecked ? $isChecked")
                    }
                }
            }
        }
        )
        explosion.setWind(wind.isChecked)
    }
}
