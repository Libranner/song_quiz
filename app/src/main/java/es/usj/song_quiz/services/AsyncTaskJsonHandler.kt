package es.usj.song_quiz.services

import android.os.AsyncTask
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

class AsyncTaskJsonHandler(private val jsonHandler: (result: String?) -> Unit):AsyncTask<String, String, String>() {
    override fun doInBackground(vararg url: String?): String {
        var text = ""
        var connection = URL(url[0]).openConnection() as HttpURLConnection
        try {
            connection.connect()
            text = connection.inputStream.use { it.reader().use { reader -> reader.readText() }}
        }
        catch  (e: Exception){
            Log.e("Error: ", e.toString())
        }
        finally {
            connection.disconnect()
        }

        return text
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        jsonHandler(result)
    }
}
