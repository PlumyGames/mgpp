package plumy.test

class TestGroovyBridge {
    fun `test call`() {
    }
}

class KtClass {
    fun methodMissing(
        name: String, args: Any,
    ) {
        val argsText = if (args is Array<*>) {
            args.joinToString(",")
        } else args.toString()
        println("methodMissing:$name,$args -> $argsText")
    }

    fun invokeMethod(
        name: String, args: Any,
    ) {
        val argsText = if (args is Array<*>) {
            args.joinToString(",")
        } else args.toString()
        println("invokeMethod:$name,$args -> $argsText")
    }
}