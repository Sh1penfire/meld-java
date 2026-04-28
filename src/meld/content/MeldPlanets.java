package meld.content;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.game.CampaignRules;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.NoiseMesh;
import mindustry.maps.generators.BlankPlanetGenerator;
import mindustry.type.Planet;

public class MeldPlanets {
    public static Planet ikaru;

    public static void load(){
        ikaru = new Planet("ikaru", Planets.sun, 1.2f, 2){{
            generator = new BlankPlanetGenerator();
            orbitRadius = 72.31f;

            clearSectorOnLose = true;
            allowLaunchSchematics = false;
            allowLaunchToNumbered = false;
            allowLaunchLoadout = false;
            allowSectorInvasion = false;
            hasAtmosphere = true;
            visible = true;
            updateLighting = false;
            defaultCore = MeldBlocks.coreRaft;
            bloom = false;

            startSector = 2;
            iconColor = Color.valueOf("f3c3e7");

            accessible = true;
            alwaysUnlocked = false;

            campaignRuleDefaults = new CampaignRules(){{
                fog = true;
                showSpawns = true;
            }};

            int seed = 0;

            mesh = new MultiMesh(
                    new NoiseMesh(this, seed, 5, 0.9f, 3, 1.4f, 1, 1, Color.valueOf("cba380"), Color.valueOf("a06a3b"), 1, 1, 1, 0.5f)
            );
        }};
    }
}
