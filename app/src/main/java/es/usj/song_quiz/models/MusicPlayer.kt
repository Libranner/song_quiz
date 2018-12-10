package es.usj.song_quiz.models

import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

@Suppress("UnusedEquals")
/**
 * Created by libranner on 11/30/18.
 */
class MusicPlayer : MediaPlayer.OnPreparedListener {
    var songs: Array<Song>
    private var mp: MediaPlayer? = null
    private var currentIndex = -1

    constructor(songs: Array<Song>) {
        this.songs = songs
    }

    val currentSong: Song?
        get() {
            if (currentIndex < songs.size) {
                return songs[currentIndex]
            }
            return null
        }

    fun stop() {
        mp?.stop()
    }

    fun playSong(filename: String) {
        try {
            mp = MediaPlayer()
            mp?.setDataSource(filename)
            mp?.setOnPreparedListener(this)
            mp?.prepareAsync();
        } catch (e: IOException) {
            Log.e("Error: ", e.toString())
        }
        catch(e: Exception) {
            Log.e("Error: ", e.toString())
        }
    }

    val duration : Int
        get() {
            return mp?.duration as Int
        }

    val currentPosition : Int
        get() {
            return mp?.currentPosition as Int
        }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
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