package es.usj.song_quiz.services

import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by libranner on 09/12/2018.
 */

class DownloadHandler(private val filesDir: File, private val fileHandler: (fileName: String?) -> Unit): AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String?): String? {
        try {
            val file = Uri.parse(params[0])?.lastPathSegment?.let { filename ->
                //TODO: Use real file name
                File(filesDir, "sound.mp3")
            }

            if(file != null) {
                file.createNewFile()
                val outputFile = FileOutputStream(file)

                //TODO: Use real url
                //val url = URL(params[0])
                val url = URL("https://firebasestorage.googleapis.com/v0/b/test-5596f.appspot.com/o/sound.mp3?alt=media&token=06c5e2f3-8217-4bdc-b2ab-6741b9dc4506")
                val conn = url.openConnection()
                val contentLength = conn.contentLength

                val stream = DataInputStream(url.openStream())

                val buffer = ByteArray(contentLength)
                stream.readFully(buffer)
                stream.close()

                val fos = DataOutputStream(outputFile)
                fos.write(buffer)
                fos.flush()
                fos.close()

                return file.name
            }

        } catch (e: FileNotFoundException) {
            Log.e("Error: ", e.toString())
        } catch (e: IOException) {
            Log.e("Error: ", e.toString())
        }

        return null
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        fileHandler(result)
    }
}
