package meld.graphics;

import arc.Core;
import arc.Events;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.GL20;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.scene.ui.layout.Scl;
import arc.util.Log;
import arc.util.Time;
import meld.Meld;
import meld.SettingKeys;
import meld.ui.MeldSettings;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.graphics.Layer;
import mindustry.graphics.LightRenderer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;

import static mindustry.Vars.renderer;


public class MeldShaders {

    public static NamedShader sonar;
    public static LightShaderMeld light;

    public static FrameBuffer lightBuffer = new FrameBuffer(), sonarBuffer = new FrameBuffer();

    public static boolean loaded = false;

    public static void load(){
        if(loaded) return;
        loaded = true;

        sonar = new SonarShader("sonar");
        light = new LightShaderMeld("light");

        Events.run(EventType.Trigger.draw, () -> {
            sonarBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

            if(Vars.state.rules.lighting && MeldSettings.replaceLighting){
                lightBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                Draw.draw(Layer.background, () -> {
                    lightBuffer.begin();
                });
            }

            Draw.drawRange(MeldLayers.sonar, 0.1f, () -> {
                sonarBuffer.begin(Color.clear);
            }, () -> {
                sonarBuffer.end();
                sonarBuffer.blit(MeldShaders.sonar);
            });

            Draw.drawRange(MeldLayers.sonarInside, 0.1f, () -> {
                renderer.effectBuffer.begin(Color.clear);
            }, () -> {
                renderer.effectBuffer.end();
                renderer.effectBuffer.blit(MeldShaders.sonar);
            });
        });
    }

    /** Shaders that the the*/
    public static class NamedShader extends Shader {
        public NamedShader(String frag) {
            super(Core.files.internal("shaders/screenspace.vert"), Vars.tree.get("shaders/" + frag + ".frag"));
        }

        @Override
        public void apply() {
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_campos",
                    Core.camera.position.x,
                    Core.camera.position.y
            );
            setUniformf("u_resolution",
                    Core.graphics.getWidth(),
                    Core.graphics.getHeight()
            );
            setUniformf("u_drawCol", Draw.getColor().r,  Draw.getColor().g,  Draw.getColor().b,  Draw.getColor().a);
        }
    }

    public static class SonarShader extends NamedShader{
        public Color sonarColor = Pal.accent;

        public SonarShader(String frag) {
            super(frag);
        }


        @Override
        public void apply() {
            super.apply();
            setUniformf("u_color",
                    sonarColor.r,
                    sonarColor.g,
                    sonarColor.b,
                    sonarColor.a
            );
        }
    }

    public static class LightShaderMeld extends NamedShader{
        public float thickness = 0;
        public Color ambient = new Color(0.01f, 0.01f, 0.04f, 0.99f);
        public Texture lights = null;
        public Texture exclusion = null;

        public LightShaderMeld(String frag) {
            super(frag);
        }

        @Override
        public void apply(){
            /*
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

             */

            setUniformf("u_ambient", ambient);
            setUniformf("u_thickness", thickness);

            if(exclusion != null) exclusion.bind(2);
            if(lights != null) lights.bind(1);

            setUniformi("u_exclusion", 2);
            setUniformi("u_lights", 1);

            lightBuffer.getTexture().bind(0);

        }
    }
}
