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

    fun release() {
        mp?.release()
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
            if(mp == null) {
                return  0
            }
            return mp!!.duration
        }

    val currentPosition : Int
        get() {
            if(mp == null) {
                return  0
            }
            return mp!!.currentPosition
        }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    fun nextSong() : Song? {
        currentIndex++
        if (currentIndex < songs.size) {
            return  songs[currentIndex]
        }
        return null
    }
}