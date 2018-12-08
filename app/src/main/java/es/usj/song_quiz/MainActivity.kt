package es.usj.song_quiz

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
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : AppCompatActivity() {

    private var songs: MutableList<Song> = ArrayList()
    private var game : Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val path = "https://firebasestorage.googleapis.com/v0/b/test-5596f.appspot.com/o/sound.mp3?alt=media&token=06c5e2f3-8217-4bdc-b2ab-6741b9dc4506"
        songs.add(Song("1","Cancion 1", "Juan Perez 1", path))
        songs.add(Song("2","Cancion 2", "Juan Perez 2", path))
        songs.add(Song("3","Cancion 3", "Juan Perez 3", path))
        songs.add(Song("4","Cancion 4", "Juan Perez 4", path))
        songs.add(Song("5","Cancion 5", "Juan Perez 5", path))
        songs.add(Song("6","Cancion 6", "Juan Perez 6", path))

        game = Game(Calendar.getInstance().time, songs.toTypedArray())
        game!!.start()

        setOptions(game!!.possibleAnswers())

        Timer("timer", true).scheduleAtFixedRate(0,1000) {
            runOnUiThread {
                val c = milliSecondsToTimer(game!!.currentDuration().toLong())
                tvTime.text = milliSecondsToTimer(game!!.currentDuration().toLong())
            }
        }
    }

    private fun onOptionClick(view: View) {
        if (game!!.gameOver) {
            Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show()
            return
        }

        if (game!!.checkSong(view.tag.toString())) {
            Toast.makeText(this, "Right answer", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Wrong answer! You dumb bastard!", Toast.LENGTH_SHORT).show()
        }
        tvScore.text = game!!.totalPoints.toString()
        continueGame()
    }

    private fun continueGame() {
        game!!.playNextSong()

        if (game!!.gameOver) {
            Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show()
        } else {
            setOptions(game!!.possibleAnswers())
        }
    }

    private fun setOptions(songs: Array<Song>) {
        if ((linearLayout as LinearLayout).childCount > 0)
            (linearLayout as LinearLayout).removeAllViews()

        Toast.makeText(this, game!!.currentSong!!.artistName, Toast.LENGTH_LONG).show()

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
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString

        // return timer string
        return finalTimerString
    }
}
