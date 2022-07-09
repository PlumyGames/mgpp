package plumy.test

import arc.Events
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
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
        if (!Vars.headless) {
            R.Sprite.load()
            R.Sound.load()
        }
        Events.on(ClientLoadEvent::class.java) {
            BaseDialog("Test Mod Kt 666").apply {
                cont.add(Image(R.Sprite.smartDistributor)).row()
                cont.add(Table().apply {
                    button("Click ME!") {
                        R.Sound.success.play()
                    }.width(150f)
                    button("Aho") {
                        R.Sound.aho.play()
                    }.width(100f)
                })
                addCloseButton()
            }.show()
        }
    }

    override fun loadContent() {
        Log.info("TestMod content loaded.")
    }
}

