package es.usj.song_quiz.models

import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

/**
 * Created by libranner on 11/30/18.
 */
class MusicPlayer {
    var songs: Array<Song>
    var mp: MediaPlayer
    var currentIndex = -1

    constructor(songs: Array<Song>) {
        this.songs = songs
        this.mp = MediaPlayer()
    }

    val currentSong: Song?
        get() {
            if (currentIndex < songs.size) {
                return songs[currentIndex]
            }
            return null
        }

    fun stop() {
        mp.stop()
    }

    fun playSong(filename: String) {
        try {
            mp = MediaPlayer()
            mp.setDataSource(filename)
            mp.prepare()
            mp.start()
        } catch (e: IOException) {
            Log.e("Error: ", e.toString())
        }
        catch(e: Exception) {
            Log.e("Error: ", e.toString())
        }
    }

    /*private fun playNextSong() {
        val song = nextSong()
        if(song != null) {
            playSong(song.filename)
        }
    }*/

    fun nextSong() : Song? {
        currentIndex++
        if (currentIndex < songs.size) {
            val song = songs[currentIndex]

            return song
        }
        return null
    }
}