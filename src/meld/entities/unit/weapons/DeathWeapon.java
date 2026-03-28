package meld.entities.unit.weapons;

import mindustry.gen.Sounds;
import mindustry.type.Weapon;

//Weapon for death explosions. Hidden and can't shoot by default. Always facing fowards.
public class DeathWeapon extends Weapon {
    public DeathWeapon(){
        super();
        x = y = 0;
        mirror = false;
        rotate = true;
        rotateSpeed = 0;
        controllable = aiControllable = false;
        reload = 60;

        shootCone = 360;
        shootSound = Sounds.none;
        shootOnDeath = true;
        display = false;
    }
}
