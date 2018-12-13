package es.usj.song_quiz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import es.usj.song_quiz.R

class AboutActivity : AppCompatActivity() {

    private lateinit var mContent: View
    private var shortAnimationDuration: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        mContent = findViewById(R.id.view1)
        mContent.visibility = View.GONE
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        mContent.apply {
           alpha = 0f
           visibility = View.VISIBLE

          animate().alpha(1f).setDuration(shortAnimationDuration.toLong()).setListener(null)
        }

        }
}
