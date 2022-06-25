package plumy.test

import arc.util.Log
import mindustry.mod.Mod

class TestModKt : Mod() {
    init {
        Log.info("TestMod instantiated.")
    }
    override fun init() {
        Log.info("TestMod inited.")
    }

    override fun loadContent() {
        Log.info("TestMod content loaded.")
    }
}