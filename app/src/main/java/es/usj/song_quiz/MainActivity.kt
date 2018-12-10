package es.usj.song_quiz

import android.content.Intent
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import es.usj.song_quiz.models.Song
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import kotlin.collections.ArrayList
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import es.usj.song_quiz.models.Game
import es.usj.song_quiz.services.ApiConstants
import es.usj.song_quiz.services.AsyncTaskJsonHandler
import es.usj.song_quiz.utilities.Constants
import es.usj.song_quiz.utilities.TimeFormatter
import org.json.JSONArray
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {
    private lateinit var game : Game
    private var songs = arrayOf<Song>()
    private lateinit var timer: TimerTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleControlVisibility(false)
        btnRestart.setOnClickListener {
            cleanup()
            startGame()
            toggleControlVisibility(true)
        }
    }

    private fun toggleControlVisibility(show: Boolean) {
        if(show) {
            btnNextSong.visibility = View.VISIBLE
            linearLayout.visibility = View.VISIBLE
            btnRestart.visibility = View.GONE
            tvScore.visibility = View.VISIBLE
        }
        else {
            btnNextSong.visibility = View.GONE
            linearLayout.visibility = View.GONE
            btnRestart.visibility = View.VISIBLE
            tvScore.visibility = View.GONE
        }
    }

    private fun startGame() {
        AsyncTaskJsonHandler(::handlerJson).execute(ApiConstants.BASE_URL)
    }

    private fun handlerJson(result: String?) {
        val jsonArray = JSONArray(result)

        val songsList = ArrayList<Song>()
        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject =  jsonArray.getJSONObject(x)

            songsList.add(Song(
                    jsonObject.getInt("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("author"),
                    jsonObject.getString("file")
                    ))
            x++
        }

        if (songsList.size > 0) {
            songsList.shuffle()
            songs = songsList.toTypedArray()
            configureGame(songs)
        }
        else {
            loadingError()
        }
    }

    private fun configureGame(songs: Array<Song>) {
        game = Game(Calendar.getInstance().time, songs, this.filesDir)
        game.start()
        setOptions(game.possibleAnswers())
        tvScore.text = game.score.toString()

        timer = Timer("timer", true).scheduleAtFixedRate(0,1000) {
            runOnUiThread {
                tvTime.text = TimeFormatter.milliSecondsToTimer(game.currentDuration().toLong())
            }
        }
    }

    private fun loadingError() {
        Toast.makeText(this, getString(R.string.problem_loading), Toast.LENGTH_LONG).show()
    }

    private fun onOptionClick(view: View) {
        playSound(game.checkSong(view.tag as Int))
        tvScore.text = game.score.toString()
        continueGame()
    }

    fun playSound(isCorrect: Boolean) {
        val sound = if (isCorrect) R.raw.correct_answer else R.raw.wrong_answer

        val mediaPlayer = MediaPlayer.create(this, sound)
        mediaPlayer.start()
        mediaPlayer.isLooping = false
        Thread.sleep(1000)
        mediaPlayer.stop()
        mediaPlayer.release();
    }

    private fun continueGame() {
        game.playNextSong()

        if (game.gameOver) {
            showGameOverScreen()
        } else {
            setOptions(game.possibleAnswers())
        }
    }

    private fun showGameOverScreen() {
        game.stop()
        timer.cancel()

        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra(Constants.SCORE_KEY, game.score)
        startActivity(intent)
        toggleControlVisibility(false)
    }

    private fun cleanup() {
        if ((linearLayout as LinearLayout).childCount > 0)
            (linearLayout as LinearLayout).removeAllViews()

        tvScore.text = getString(R.string._0)
        tvTime.text = getString(R.string._00_00)
    }

    private fun setOptions(songs: Array<Song>) {
        if ((linearLayout as LinearLayout).childCount > 0)
            (linearLayout as LinearLayout).removeAllViews()

        Toast.makeText(this, game.currentSong!!.artistName, Toast.LENGTH_LONG).show()

        for (i in 0 until songs.size) {
            val btnOption = Button(this)
            btnOption.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            btnOption.setOnClickListener({ onOptionClick(btnOption) })

            btnOption.text = songs[i].artistName
            btnOption.tag = songs[i].id

            linearLayout.addView(btnOption)
        }
    }
}

