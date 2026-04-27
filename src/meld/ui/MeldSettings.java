package meld.ui;

import arc.Core;
import arc.util.Log;
import meld.Meld;
import meld.SettingKeys;
import meld.content.MeldUnits;
import mindustry.Vars;
import mindustry.content.Blocks;

public class MeldSettings {
    public static boolean replaceLighting, invertAmbient, connectedSonar,

    //The expirimental settings
    bulbheadOmnimove, portSaves;
    public static float lightScale = 1;

    public static void loadSettings(){

        replaceLighting = Core.settings.getBool(SettingKeys.lighting, true);
        invertAmbient = Core.settings.getBool(SettingKeys.invertAmbient, false);
        connectedSonar = Core.settings.getBool(SettingKeys.connectedSonar, true);
        lightScale = Core.settings.getFloat(SettingKeys.unitLightScale, 1);

        bulbheadOmnimove = Core.settings.getBool(SettingKeys.bulbheadOmnimove, false);

        portSaves = Core.settings.getBool(SettingKeys.portSaves, false);

        Vars.ui.settings.addCategory(Core.bundle.get("settings.meld-title"), Meld.prefix("icon"), t -> {
            t.checkPref(Core.bundle.get(SettingKeys.lighting), true, b -> {
                replaceLighting = b;
            });
            t.checkPref(Core.bundle.get(SettingKeys.invertAmbient), false, b -> {
                invertAmbient = b;
            });

            t.checkPref(Core.bundle.get(SettingKeys.connectedSonar), true, b -> {
                connectedSonar = b;
            });

            t.sliderPref(Core.bundle.get(SettingKeys.unitLightScale), 100, 1, 200, f -> {
                float prevScale = lightScale;
                lightScale = f/100f;

                float multi = lightScale/prevScale;
                Vars.content.units().each(u -> {
                    u.lightRadius *= multi;
                });

                return f + "%";
            });
        });
        Vars.ui.settings.addCategory(Core.bundle.get("settings.meld-expirimental"), Meld.prefix("icon"), t -> {
            t.checkPref(Core.bundle.get(SettingKeys.bulbheadOmnimove), false, MeldUnits::bulbheadOmnimove);

            t.checkPref(Core.bundle.get(SettingKeys.portSaves), false, b -> {
                portSaves = b;
            });
        });

        /*
        photosensitiveMode = Core.settings.getBool("settings.frostscape-flashing-lights-safety", false);
        heatOverlay.enabled = Core.settings.getBool("settings.frostscape-heat-overlay", false);
        simplifiedLightning = Core.settings.getBool("settings.frostscape-simple-lightning");
        parallax = Core.settings.getInt("settings.frostscape-parallax");

        heatOverlay.enabled = Core.settings.getBool("settings.frostscape-heat-overlay", false);
        ui.settings.addCategory(Core.bundle.get("settings.frostscape-title"), NAME + "-hunter", t -> {
            t.sliderPref(Core.bundle.get("settings.frostscape-parallax"), 100, 1, 100, 1, s -> {
                parallax = s/100;
                return s + "%";
            });
            t.sliderPref(Core.bundle.get("settings.frostscape-wind-visual-force"), 1, 0, 8, 1, s -> s * 100 + "%");
            t.checkPref(Core.bundle.get("settings.frostscape-flashing-lights-safety"), false, b -> {
                photosensitiveMode = b;
            });
            t.checkPref(Core.bundle.get("settings.frostscape-heat-overlay"), false, b -> {
                heatOverlay.enabled = b;
            });
            t.checkPref(Core.bundle.get("settings.frostscape-simple-lightning"), false, b -> {
                simplifiedLightning = b;
            });
            t.row();
            t.add(Core.bundle.get("settings.frostscape.flashingwarning")).wrap().left().growX().padTop(3);
        });

         */
    }

}
