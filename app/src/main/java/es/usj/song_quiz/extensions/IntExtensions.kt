package es.usj.song_quiz.extensions

import java.util.*

/**
 * Created by libranner on 11/25/18.
 */

fun IntRange.random() =
        Random().nextInt(endInclusive - start) +  start