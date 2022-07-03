package plumy.test;

import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.mod.Mod;

public class MainMod extends Mod {
    public MainMod() {
        super();
        Log.info("MainMod.ctor.");
    }

    @Override
    public void init() {
        super.init();
        Log.info("MainMod.init");
    }

    @Override
    public void loadContent() {
        super.loadContent();
        Log.info("MainMod.loadContent");
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        super.registerServerCommands(handler);
        Log.info("MainMod.registerServerCommands");
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        super.registerClientCommands(handler);
        Log.info("MainMod.registerClientCommands");
    }
}
