package es.usj.song_quiz.models

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import es.usj.song_quiz.extensions.random
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by libranner on 11/25/18.
 */
class Game {
    val startDate: Date
    var finishDate: Date? = null
    var totalPoints = 0
    private var player: MusicPlayer
    val numberOfWrongAnswers = 2
    var gameOver = false

    constructor(startDate: Date, songs: Array<Song>) {
        this.startDate = startDate
        this.player = MusicPlayer(songs)
    }

    val currentSong: Song?
        get() {
            return player.currentSong
        }

    fun start() {
        player.nextSong()
    }

    fun currentDuration() : Int {
        return player.mp.currentPosition
    }

    fun playNextSong() {
        if(gameOver)
            return

        val song = player.nextSong()
        if(song == null) {
            gameOver = true
        }
    }

    fun possibleAnswers(): Array<Song> {
        if (currentSong == null) {
            return arrayOf()
        }

        val answers: MutableList<Song> = ArrayList()
        val allSongs = player.songs.toMutableList()
        allSongs.remove(currentSong!!)

        answers.add(currentSong!!)

        for (i in 0 until numberOfWrongAnswers) {
            val randomIndex = (0 until allSongs.size).random()
            answers.add(allSongs[randomIndex])
            allSongs.removeAt(randomIndex)
        }

        answers.shuffle()

        return answers.toTypedArray()
    }

    fun checkSong(selectedId: String) : Boolean {
        if (currentSong?.id == selectedId) {
            calculatePoints()
            return true
        }

        return false
    }

    private fun calculatePoints() {
        var timePoints: Double

        if(player.mp.currentPosition == 0) {
            timePoints = 1.0
        }
        else {
            timePoints = (player.mp.duration - player.mp.currentPosition).toDouble()/player.mp.currentPosition.toDouble()
            timePoints = Math.round(timePoints * 100.0) / 100.0
        }

        totalPoints += (timePoints * 100).toInt()
    }
}