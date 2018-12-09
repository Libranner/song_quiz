package es.usj.song_quiz.models

import android.media.MediaPlayer
import android.util.Log
import es.usj.song_quiz.services.ApiConstants
import es.usj.song_quiz.services.DownloadHandler
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
            //TODO: Pass real path:
            val path = "https://firebasestorage.googleapis.com/v0/b/test-5596f.appspot.com/o/sound.mp3?" +
                    "alt=media&token=06c5e2f3-8217-4bdc-b2ab-6741b9dc4506"

            DownloadHandler(song.fileName).execute(path)
            val url = "${ApiConstants.baseUrl}/${song.fileName}"

            mp.setDataSource(path)
            mp.prepare()
            mp.start()
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