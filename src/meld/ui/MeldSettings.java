package meld.ui;

import arc.Core;
import arc.func.Boolc;
import arc.func.Cons;
import arc.util.Log;
import meld.Meld;
import meld.SettingKeys;
import meld.content.MeldUnits;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.io.SaveFileReader;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class MeldSettings {
    public static boolean replaceLighting, invertAmbient, connectedSonar, overlayOverFog,

    //The expirimental settings
    bulbheadOmnimove, portSaves;
    public static float lightScale = 100;

    public static void check(SettingsMenuDialog.SettingsTable table, String settingName, boolean def, Boolc changed){
        table.checkPref(Core.bundle.get(settingName), def, b -> {
            Core.settings.put(settingName, b);
            changed.get(b);
        });
    }

    public static void loadSettings(){

        replaceLighting = Core.settings.getBool(SettingKeys.lighting, true);
        invertAmbient = Core.settings.getBool(SettingKeys.invertAmbient, false);
        connectedSonar = Core.settings.getBool(SettingKeys.connectedSonar, true);
        overlayOverFog = Core.settings.getBool(SettingKeys.overlayOverFog, true);

        float newLightScale = Core.settings.getInt(SettingKeys.unitLightScale, 100);
        MeldUnits.lightRadiusMultiplier(newLightScale);
        lightScale = newLightScale;

        bulbheadOmnimove = Core.settings.getBool(SettingKeys.bulbheadOmnimove, false);

        portSaves = Core.settings.getBool(SettingKeys.portSaves, false);

        //TODO: Move save io related things to their own class?
        if(portSaves){
            Vars.content.each(c -> {
                if(!(c instanceof UnlockableContent content) || content.minfo.mod == null) return;
                String modified = content.name;
                String[] bits = modified.split("meld2");

                if(bits.length > 1){
                    String oldName = "meld" + bits[1];

                    SaveFileReader.fallback.put(oldName, modified);
                }
            });
        }

        Vars.ui.settings.addCategory(Core.bundle.get("settings.meld-title"), Meld.prefix("icon"), t -> {
            check(t, SettingKeys.lighting, true, b -> replaceLighting = b);
            check(t, SettingKeys.invertAmbient, true, b -> invertAmbient = b);
            check(t, SettingKeys.connectedSonar, true, b -> connectedSonar = b);
            check(t, SettingKeys.overlayOverFog, true, b -> overlayOverFog = b);

            t.sliderPref(Core.bundle.get(SettingKeys.unitLightScale), 100, 1, 200, f -> {
                MeldUnits.lightRadiusMultiplier(f);
                lightScale = f;
                Core.settings.put(SettingKeys.unitLightScale, f);
                return f + "%";
            });
        });

        Vars.ui.settings.addCategory(Core.bundle.get("settings.meld-experimental"), Meld.prefix("icon"), t -> {
            check(t, SettingKeys.bulbheadOmnimove, false, MeldUnits::bulbheadOmnimove);
            check(t, SettingKeys.portSaves, false, b -> portSaves = b);
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
