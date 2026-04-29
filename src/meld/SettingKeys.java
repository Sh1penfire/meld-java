package meld;

public class SettingKeys {
    public static String pref(String in){
        return "setting.meld-" + in;
    }

    public static String lighting = pref("lighting"),
    invertAmbient = pref("invert-ambient"),
    connectedSonar = pref("connected-sonar"),
    unitLightScale = pref("unit-light-scale"),
    overlayOverFog = pref("highvis-overlay"),
    shitpostBundles = pref("shitpost-bundles"),

    //Expirimental- doesn't sync in multiplayer and off by default
    bulbheadOmnimove = pref("bulbhead-omnimove"),
    portSaves = pref("port-saves");
}
