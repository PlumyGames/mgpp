package plumy.test

class GroovyTest {
    static def main(def args) {
        new KtClass().with {
            it.test("this is arg")
        }
    }
}
