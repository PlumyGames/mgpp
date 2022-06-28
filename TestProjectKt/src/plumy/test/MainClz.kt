package plumy.test

import arc.Events
import arc.scene.ui.Image
import arc.util.Log
import mindustry.Vars
import mindustry.game.EventType.ClientLoadEvent
import mindustry.mod.Mod
import mindustry.ui.dialogs.BaseDialog

class TestModKt : Mod() {
    init {
        Log.info("TestMod instantiated.")
    }

    override fun init() {
        Log.info("TestMod inited.")
        Events.on(ClientLoadEvent::class.java) {
            BaseDialog("Test Mod Kt 666").apply {
                cont.add(Image(R.Sprite.smartDistributor))
                addCloseButton()
            }.show()
        }
    }

    override fun loadContent() {
        Log.info("TestMod content loaded.")
        if (!Vars.headless) {
            R.Sprite.load()
        }
    }
}