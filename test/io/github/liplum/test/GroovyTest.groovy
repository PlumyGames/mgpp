package io.github.liplum.test

import plumy.test.KtClass

class GroovyTest {
    static def main(def args) {
        new KtClass().with {
            it.test("this is arg")
        }
    }
}
