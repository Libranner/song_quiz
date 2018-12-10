package es.usj.song_quiz.models

import es.usj.song_quiz.extensions.random
import es.usj.song_quiz.services.ApiConstants
import es.usj.song_quiz.services.DownloadHandler
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by libranner on 11/25/18.
 */
class Game {
    val startDate: Date
    var finishDate: Date? = null
    var score = 0
    private var player: MusicPlayer
    val numberOfWrongAnswers = 2
    var gameOver = false
    var directory: File

    constructor(startDate: Date, songs: Array<Song>, directory: File) {
        this.startDate = startDate
        this.player = MusicPlayer(songs)
        this.directory = directory
    }

    val currentSong: Song?
        get() {
            return player.currentSong
        }

    fun start() {
        playNextSong()
    }

    fun currentDuration() : Int {
        return player.currentPosition
    }

    fun playNextSong() {
        if(gameOver)
            return

        val song = player.nextSong()
        if(song != null) {
            playSongFromFile(song.filename)
        }
        else {
            gameOver = true
        }
    }

    private fun playSongFromFile(filename: String) {
        player.stop()

        //val file = File(directory, filename)
        val file = File(directory, "sound.mp3")
        if (file.exists()) {
            player.playSong(file.absolutePath)
        }
        else {
            //TODO: Contruct real path
            val path = "$ApiConstants.BASE_URL/$filename"
            DownloadHandler(directory, ::fileHandler).execute(filename)
        }
    }

    private fun fileHandler(filename: String?) {
        if(filename != null) playSongFromFile(filename)
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

    fun checkSong(selectedId: Int) : Boolean {
        player.stop()
        if (currentSong?.id == selectedId) {
            calculatePoints()
            return true
        }

        return false
    }

    private fun calculatePoints() {
        var timePoints: Double

        if(player.currentPosition == 0) {
            timePoints = 1.0
        }
        else {
            timePoints = (player.duration - player.currentPosition).toDouble()/player.currentPosition.toDouble()
            timePoints = Math.round(timePoints * 100.0) / 100.0
        }

        score += (timePoints * 100).toInt()
    }

    fun stop() {
        finishDate = Calendar.getInstance().time
        player.stop()
    }
}