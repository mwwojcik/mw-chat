package mw.chat.reactor.operators

import java.io.File


fun main() {
    File("src/test/java/mw/chat/reactor/operators").list().forEach {

        var result="https://github.com/mwwojcik/mw-chat/blob/main/src/test/java/mw/chat/reactor/operators/"+it+"[See: "+it+"]"
        println("=== "+it.split(".")[0].replace("Test","").replace("Operator",""))
        println(result)

    }

}