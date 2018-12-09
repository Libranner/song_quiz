package es.usj.song_quiz

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import es.usj.song_quiz.models.Song
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import kotlin.collections.ArrayList
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import es.usj.song_quiz.models.Game
import es.usj.song_quiz.services.ApiConstants
import es.usj.song_quiz.services.AsyncTaskJsonHandler
import org.json.JSONArray
import java.io.File
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {
    private lateinit var game : Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //startGame()

        val path = "https://firebasestorage.googleapis.com/v0/b/test-5596f.appspot.com/o/sound.mp3?" +
                "alt=media&token=06c5e2f3-8217-4bdc-b2ab-6741b9dc4506"

        val directory = this.filesDir
        val file = File(directory, "sound.mp3")
        if (file.exists()) {
            Log.e("asdas: ", "asasasasasa")
        }

        val c = getTempFile(this, path)


        val file2 = File(directory, "sound.mp3")

    }

    private fun startGame() {
        AsyncTaskJsonHandler(::handlerJson).execute(ApiConstants.baseUrl)
    }

    private fun handlerJson(result: String?) {
        val jsonArray = JSONArray(result)

        val songs = ArrayList<Song>()
        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject =  jsonArray.getJSONObject(x)

            songs.add(Song(
                    jsonObject.getInt("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("author"),
                    jsonObject.getString("file")
                    ))
            x++
        }

        if (songs.size > 0) {
            songs.shuffle()
            game = Game(Calendar.getInstance().time, songs.toTypedArray())
            game.start()
            setOptions(game.possibleAnswers())

            Timer("timer", true).scheduleAtFixedRate(0,1000) {
                runOnUiThread {
                    tvTime.text = milliSecondsToTimer(game.currentDuration().toLong())
                }
            }
        }
        else {
            loadingError()
        }
    }

    private fun getTempFile(context: Context, url: String): File? =
            Uri.parse(url)?.lastPathSegment?.let { filename ->
                File(context.filesDir, filename)
            }

    private fun loadingError() {
        Toast.makeText(this, getString(R.string.problem_loading), Toast.LENGTH_LONG).show()
    }

    private fun onOptionClick(view: View) {
        if (game.gameOver) {
            showGameOverScreen()
            return
        }

        if (game.checkSong(view.tag as Int)) {
            Toast.makeText(this, "Right answer", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Wrong answer! You dumb bastard!", Toast.LENGTH_SHORT).show()
        }
        tvScore.text = game.totalPoints.toString()
        continueGame()
    }

    private fun continueGame() {
        game.playNextSong()

        if (game.gameOver) {
            game.stop()
        } else {
            setOptions(game.possibleAnswers())
        }
    }

    private fun showGameOverScreen() {
        //TODO: Game Over Screen
        Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show()
    }

    private fun setOptions(songs: Array<Song>) {
        if ((linearLayout as LinearLayout).childCount > 0)
            (linearLayout as LinearLayout).removeAllViews()

        Toast.makeText(this, game.currentSong!!.artistName, Toast.LENGTH_LONG).show()

        for(i in 0 until songs.size) {
            val btnOption = Button(this)
            btnOption.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            btnOption.setOnClickListener({ onOptionClick(btnOption) })

            btnOption.text = songs[i].artistName
            btnOption.tag = songs[i].id

            linearLayout.addView(btnOption)
        }
    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var result = ""
        var secondsString: String

        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        if (hours > 0) {
            result = hours.toString() + ":"
        }

        secondsString = when {
            seconds < 10 -> "0$seconds"
            else -> "" + seconds
        }

        result = "$result$minutes:$secondsString"

        return result
    }
}

