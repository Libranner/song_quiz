package es.usj.song_quiz.utilities

/**
 * Created by libranner on 10/12/2018.
 */
class TimeFormatter {
    companion object {
        fun milliSecondsToTimer(milliseconds: Long): String {
            var result = ""
            var secondsString: String

            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

            if (hours > 0) {
                result = hours.toString() + ":"
            }

            secondsString = when {
                seconds < 10 -> "0$seconds"
                else -> "" + seconds
            }

            result = "$result$minutes:$secondsString"

            return result
        }
    }
}