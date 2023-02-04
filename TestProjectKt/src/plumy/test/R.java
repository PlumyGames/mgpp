package plumy.test;

import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader.SoundParameter;
import arc.audio.Sound;
import arc.graphics.g2d.TextureRegion;

public final class R {
    public static final class Sprites {
        public static TextureRegion cloudCloud;
        public static TextureRegion cloudDataTransferAnim;
        public static TextureRegion cloudShredderAnim;
        public static TextureRegion cloud;
        public static TextureRegion receiver;
        public static TextureRegion smartDistributorArrowsAnim;
        public static TextureRegion smartDistributor;
        public static TextureRegion smartUnloaderCover;
        public static TextureRegion smartUnloaderShrinkAnim;
        public static TextureRegion smartUnloader;

        public static void load() {
            cloudCloud = arc.Core.atlas.find("mgpp-kt-cloud-cloud");
            cloudDataTransferAnim = arc.Core.atlas.find("mgpp-kt-cloud-data-transfer-anim");
            cloudShredderAnim = arc.Core.atlas.find("mgpp-kt-cloud-shredder-anim");
            cloud = arc.Core.atlas.find("mgpp-kt-cloud");
            receiver = arc.Core.atlas.find("mgpp-kt-receiver");
            smartDistributorArrowsAnim = arc.Core.atlas.find("mgpp-kt-smart-distributor-arrows-anim");
            smartDistributor = arc.Core.atlas.find("mgpp-kt-smart-distributor");
            smartUnloaderCover = arc.Core.atlas.find("mgpp-kt-smart-unloader-cover");
            smartUnloaderShrinkAnim = arc.Core.atlas.find("mgpp-kt-smart-unloader-shrink-anim");
            smartUnloader = arc.Core.atlas.find("mgpp-kt-smart-unloader");
        }
    }

    public static final class Sounds {
        protected static Sound loadSound(String soundName) {
            Sound sound = new Sound();
            AssetDescriptor<?> desc = arc.Core.assets.load("sounds/" + soundName, Sound.class, new SoundParameter(sound));
            desc.errored = Throwable::printStackTrace;
            return sound;
        }

        public static Sound aho;
        public static Sound success;

        public static void load() {
            aho = loadSound("aho.mp3");
            success = loadSound("success.mp3");
        }
    }
}
