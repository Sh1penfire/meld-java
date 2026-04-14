package meld.meta;

import arc.Core;
import arc.util.Strings;
import meld.fluid.AspectGroup;
import mindustry.gen.Tex;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

public class MeldStats {

    public static Stat
        aspectStats = new Stat("aspect-stats");

    public static String percent(float number){
        return (int) (number * 100) + "%";
    }

    public static void aspectStats(Liquid aspect, Stats stats){
        stats.add(aspectStats, table -> {

            table.row();

            AspectGroup.groups.each(g -> {
                AspectGroup.AspectStats aspectStat = g.stats.get(aspect);
                if(aspectStat == null) return;
                table.table(Styles.grayPanel, pannel -> {

                    pannel.left();

                    pannel.add(g.localizedName).left();
                    pannel.row();
                    pannel.add("[lightgray]" + Core.bundle.get("stat.aspect-efficiency") + ":[] " + percent(g.getEfficiency(aspect)));
                    pannel.row();
                    pannel.add("[lightgray]" + Core.bundle.get("stat.aspect-density") + ":[] " + percent(g.getDensity(aspect))).left();
                    pannel.row();
                }).growX().pad(5);
                table.row();
            });
        });
    }
}
