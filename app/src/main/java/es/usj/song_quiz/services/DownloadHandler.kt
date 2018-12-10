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
                File(filesDir, filename)
            }

            if(file != null) {
                file.createNewFile()
                val outputFile = FileOutputStream(file)

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
