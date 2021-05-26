package mw.chat.reactor.operators

import java.io.File


fun main() {
    File("src/test/java/mw/chat/reactor/backpressure").walkTopDown().forEach {

        var result="https://github.com/mwwojcik/mw-chat/blob/main/src/test/java/mw/chat/reactor/"+it.parentFile.name+"/"+it.name+"[See: "+it.name+"]"
        println(result)

    }

}