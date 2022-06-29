package plumy.test;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import mindustry.Vars;
import mindustry.mod.Mod;

public class TestModGroovy extends Mod {
    public TestModGroovy() {
        super();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void loadContent() {
        super.loadContent();
    }
    protected static Sound loadSound(String soundName) {
        String name = "sounds/" + soundName;
        String path = Vars.tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

        Sound sound = new Sound();

        AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundLoader.SoundParameter(sound));
        desc.errored = Throwable::printStackTrace;

        return sound;
    }
}
