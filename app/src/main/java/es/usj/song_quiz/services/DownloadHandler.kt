package es.usj.song_quiz.services

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by libranner on 09/12/2018.
 */

class DownloadHandler(private val fileName: String): AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String?): String {
        try {
            val path = Environment.getExternalStorageDirectory().path

            val myFile = File("$path/$fileName")
            myFile.createNewFile()
            val outputFile = FileOutputStream(myFile)

            val url = URL(params[0])
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
        } catch (e: FileNotFoundException) {
            Log.e("Error: ", e.toString())
        } catch (e: IOException) {
            Log.e("Error: ", e.toString())
        }

        return ""
    }
}
