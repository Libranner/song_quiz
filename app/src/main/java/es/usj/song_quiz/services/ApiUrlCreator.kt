package es.usj.song_quiz.services

/**
 * Created by libranner on 09/12/2018.
 */
class ApiUrlCreator {
    companion object {
        private const val BASE_URL = "https://firebasestorage.googleapis.com/v0/b/test-5596f.appspot.com/o/"
        private const val QUERY_STRING = "?alt=media&token=0469a59a-e0c8-4073-9560-c68f804484b4"

        fun createURL(path: String): String {
            return "$BASE_URL$path$QUERY_STRING".replace(" ".toRegex(), "%20")
        }
    }
}