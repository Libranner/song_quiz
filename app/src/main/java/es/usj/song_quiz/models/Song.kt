package es.usj.song_quiz.models

/**
 * Created by libranner on 11/24/18.
 */
class Song {
    var id: String
    var name: String
    var artistName: String
    var path: String

    constructor(id: String, name: String, artistName: String, path: String) {
        this.id = id
        this.name = name
        this.artistName = artistName
        this.path = path
    }
}