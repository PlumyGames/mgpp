package plumy.test;

import arc.util.Log;
import mindustry.mod.Mod;

public class CoreMod extends Mod {
    public CoreMod() {
        super();
        Log.info("CoreMod.ctor.");
    }

    @Override
    public void init() {
        super.init();
        Log.info("CoreMod.init");
    }

    @Override
    public void loadContent() {
        super.loadContent();
        Log.info("CoreMod.loadContent");
    }
}
