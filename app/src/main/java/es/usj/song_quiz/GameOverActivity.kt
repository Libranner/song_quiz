package es.usj.song_quiz

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.R.id.edit
import android.content.SharedPreferences
import es.usj.song_quiz.utilities.Constants
import kotlinx.android.synthetic.main.activity_game_over.*


class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val score = intent.getIntExtra(Constants.SCORE_KEY, 0)
        tvScore.text = getString(R.string.current_score, score)

        val currentHighestScore = getHighestScore()
        when {
            currentHighestScore < score -> {
                saveScore(score)
                tvHighestScore.text = getString(R.string.new_high_score)
            }
            currentHighestScore > 0 -> tvHighestScore.text = getString(R.string.previous_highest_score, currentHighestScore)
            currentHighestScore == 0 -> tvHighestScore.text = getString(R.string.new_high_score)
        }
    }

    private fun saveScore(score: Int) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(getString(R.string.saved_high_score_key), score)
        editor.apply()
    }

    private fun getHighestScore() : Int {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getInt(getString(R.string.saved_high_score_key), 0)
    }
}
