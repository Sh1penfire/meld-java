package meld.world.blocks;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.entities.Units;
import mindustry.gen.Building;

public class Bruisekit extends FieldPulsar{

    Seq<Building> tmpDamaged = new Seq<>();

    int targetTimer = timers++;

    public float healSpeed = 2;
    public float retargetInterval = 5;

    public float recentDamageMultiplier = 0.1f;

    public Bruisekit(String name) {
        super(name);
        fogRadius = 0;
    }

    public class BruisekitBuild extends PulsarBuild{
        public Building target;


        @Override
        public void updateTile() {
            super.updateTile();
            if(timer.get(targetTimer, retargetInterval)) findTarget();
            if(target != null){
                if(target.isValid() && target.damaged()) {
                    float healAmount = healSpeed * edelta();
                    if(target.wasRecentlyDamaged()) healAmount *= recentDamageMultiplier;
                    Log.info(healAmount);

                    target.heal(healAmount);
                }
            }
        }

        public void findTarget(){
            tmpDamaged.clear();

            Units.nearbyBuildings(x, y, smoothRadius, b -> {
                if(b.team == team() && b.damaged() && b.block.size > 1) tmpDamaged.add(b);
            });

            tmpDamaged.sort(b -> b.block.size + (1 - b.healthf()) + Mathf.log(10, b.block.health)/10);
            if(!tmpDamaged.isEmpty()) target = tmpDamaged.pop();
        }

        @Override
        public void draw() {
            super.draw();
            Draw.alpha(1);
            if(target != null) Lines.line(x, y, target.x, target.y);
        }
    }
}
