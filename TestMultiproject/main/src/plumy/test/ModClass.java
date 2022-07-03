package plumy.test;

import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.mod.Mod;

public class ModClass extends Mod {
    public ModClass() {
        super();
        Log.info("ModClass.ctor.");
    }

    @Override
    public void init() {
        super.init();
        Log.info("ModClass.init");
    }

    @Override
    public void loadContent() {
        super.loadContent();
        Log.info("ModClass.loadContent");
    }

    @Override
    public void registerServerCommands(CommandHandler handler) {
        super.registerServerCommands(handler);
        Log.info("ModClass.registerServerCommands");
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        super.registerClientCommands(handler);
        Log.info("ModClass.registerClientCommands");
    }
}
