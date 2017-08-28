package com.yung_coder.oluwole.akeko.models

/**
 * Created by yung on 8/23/17.
 */

class Models {
    var language: List<lang>? = null
    var books: List<book>? = null
    var videos: List<video>? = null

    class lang {
        var _id: Int = 0
        var name: String = ""

        constructor()

        constructor(name: String) {
            this.name = name
        }
    }

    class book {
        var lang_id: String = ""
        var title: String = ""
        var copyright: String = ""
        var page_num: Int = 0
        var type: String = ""

        constructor()

        constructor(lang_id: String, title: String, copyright: String, page_num: Int, type: String) {
            this.title = title
            this.copyright = copyright
            this.lang_id = lang_id
            this.page_num = page_num
            this.type = type
        }
    }

    class video {
        var lang_id: String = ""
        var title: String = ""
        var copyright: String = ""
        var type: String = ""

        constructor()

        constructor(lang_id: String, title: String, copyright: String, type: String) {
            this.title = title
            this.copyright = copyright
            this.lang_id = lang_id
            this.type = type
        }
    }

}