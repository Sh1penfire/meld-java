package meld.world.blocks.fluid;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import meld.Meld;
import meld.content.MeldBullets;
import meld.content.MeldLiquids;
import meld.entities.bullet.TransitionBulletType;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.LiquidBulletType;
import mindustry.graphics.Drawf;

public class AspectBomb extends VisualAspectPipe{
    public AspectBomb(String name) {
        super(name);
        buildTime = 0;
        rebuildable = true;

        destroyBullet = MeldBullets.aspectBombExplosion;
        destroyBulletSameTeam = false;
    }

    public float pressureDamage = 5f;
    public float explosionPressure = 0.5f;

    public class AspectBombBuild extends VisualPipeBuild{
        @Override
        public void updateTile() {
            super.updateTile();
            if(liquids.currentAmount()/liquidCapacity > explosionPressure) damageContinuousPierce(pressureDamage);
        }
    }
}
