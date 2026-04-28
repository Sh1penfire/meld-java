package meld.entities.unit.type;

import arc.util.Log;
import meld.content.MeldUnits;
import meld.ui.MeldSettings;
import mindustry.graphics.Pal;

public class BulbheadUnitType extends MeldUnitType{

    public BulbheadUnitType(String name) {
        super(name);
        healColor = Pal.accent;
    }

    @Override
    public void init() {
        super.init();

        Log.info("UNIT");
        Log.info(MeldSettings.bulbheadOmnimove);
        MeldUnits.bulbheadOmnimove(MeldSettings.bulbheadOmnimove);
    }
}
