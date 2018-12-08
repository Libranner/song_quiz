package es.usj.song_quiz.models

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
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

    private fun playSong(song: Song) {
        try {
            mp = MediaPlayer()
            mp.setDataSource(song.path)
            mp.prepare()
            mp.start()
            //TODO: tvTime.post(mUpdateTime)
        } catch (e: IOException) {
            Log.e("Error: ", e.toString())
        }
        catch(e: Exception) {
            Log.e("Error: ", e.toString())
        }
    }

    fun nextSong() : Song? {
        currentIndex++
        if (currentIndex < songs.size) {
            val song = songs[currentIndex]
            playSong(song)
            return song
        }
        return null
    }
}