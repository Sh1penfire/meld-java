package meld.graphics;

import arc.Core;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;

public class TextModifiers {

    public static char[] glitchChars = new char[]{'-', '=', ',', '&'};

    //Modifies the input string
    public static String glitchy(String input, float edgeMultiplier, float baseChance, float speed){
        if(baseChance == 0) return input;
        Mathf.rand.setSeed((int)(Time.globalTime/60 * speed));

        StringBuilder output = new StringBuilder();
        for (float i = 0; i < input.length(); i++) {
            int j = (int)i;
            float chance = Mathf.lerp(baseChance, baseChance * edgeMultiplier, Interp.slope.apply(i/input.length()));
            if(!Mathf.chance(chance * (1 + edgeMultiplier) * Interp.slope.apply(i/input.length()))){
                output.append(input.charAt(j));
                continue;
            }
            output.append(glitchChars[Mathf.ceilPositive(Mathf.random(glitchChars.length) - 1)]);
        }
        return output.toString();
    }

    //Modifies the input string
    public static String glitchyNumb(float number, float chance, float speed){
        if(chance == 0) return String.valueOf(number);
        Mathf.rand.setSeed((int)(Time.globalTime/60 * speed));
        if(Mathf.chance(chance)) return String.valueOf((int)Mathf.range(number));
        return String.valueOf(number);
    }

    //Modifies the input string
    public static String glitchyEntry(String input, float edgeMultiplier, float baseChance, float speed){
        return glitchy(Core.bundle.get(input), edgeMultiplier, baseChance, speed);
    }
}
