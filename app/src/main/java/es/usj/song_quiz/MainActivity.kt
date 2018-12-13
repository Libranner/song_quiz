package es.usj.song_quiz

import android.animation.ValueAnimator
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
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import es.usj.song_quiz.models.Game
import es.usj.song_quiz.services.ApiUrlCreator
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
    private lateinit var speakerAnimator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleControlVisibility(false)
        btnRestart.setOnClickListener {
            cleanup()
            startGame()
            toggleControlVisibility(true)
            animateSpeaker()
        }

        val btnAbout: Button = findViewById(R.id.button)
        btnAbout.setOnClickListener{
            val about = Intent(this, AboutActivity::class.java)
            startActivity(about)
        }
    }

    private fun animateSpeaker() {
        speakerAnimator = ValueAnimator.ofFloat(0.8f, 1f)
        speakerAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            ivSpeaker.scaleX = value
            ivSpeaker.scaleY = value
        }
        speakerAnimator.repeatMode = ValueAnimator.REVERSE
        speakerAnimator.repeatCount = 10000 //this is the duration
        speakerAnimator.duration = 300L //this is the speed
        speakerAnimator.start()
    }

    private fun toggleControlVisibility(show: Boolean) {
        if(show) {
            ivSpeaker.visibility = View.VISIBLE
            linearLayout.visibility = View.VISIBLE
            btnRestart.visibility = View.GONE
            tvScore.visibility = View.VISIBLE
            tvTime.visibility = View.VISIBLE
            button.visibility = View.GONE
        }
        else {
            ivSpeaker.visibility = View.GONE
            linearLayout.visibility = View.GONE
            btnRestart.visibility = View.VISIBLE
            tvScore.visibility = View.GONE
            tvTime.visibility = View.GONE
            button.visibility = View.VISIBLE
        }
    }

    private fun startGame() {
        AsyncTaskJsonHandler(::handlerJson).execute(ApiUrlCreator.createURL("songs.json"))
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
        tvTime.text = getString(R.string._00_00)
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
        speakerAnimator.cancel()

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
        toggleControlVisibility(false)
    }

    private fun setOptions(songs: Array<Song>) {
        if ((linearLayout as LinearLayout).childCount > 0)
            (linearLayout as LinearLayout).removeAllViews()

        for (i in 0 until songs.size) {
            val btnOption = Button(this)
            btnOption.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            btnOption.setOnClickListener({
                onOptionClick(btnOption)
                val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade)
                linearLayout.startAnimation(animation)
            })

            btnOption.text = songs[i].artistName
            btnOption.tag = songs[i].id

            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
            lp.setMargins(0, 0, 0, 20)
            lp.weight = 1f

            btnOption.layoutParams = lp
            btnOption.setBackgroundColor(getColor(R.color.colorButton))
            btnOption.setTextColor(getColor(R.color.colorWhite))

            linearLayout.addView(btnOption)
        }
    }
}

