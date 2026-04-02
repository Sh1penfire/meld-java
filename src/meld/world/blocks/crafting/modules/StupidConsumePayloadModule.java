package meld.world.blocks.crafting.modules;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

public class StupidConsumePayloadModule extends CrafterModule{
    public PayloadStack[] payloads;
    /// Pin to store consumption progress.
    public int progressPin;
    /// Pin to provide efficiency on.
    public int providePin;
    public float time = 60f;

    public float baseEfficiency = 0f;
    public float efficiencyIncrease = 1f;

    public StupidConsumePayloadModule(int providePin, int progressPin){
        this.providePin = providePin;
        this.progressPin = progressPin;
    }

    @Override
    public void update(ModularCrafterBuild build){
        float provide = build.getPin(providePin);
        float consumed = (efficiencyIncrease - provide - baseEfficiency) / (efficiencyIncrease - baseEfficiency);
        float progress = build.getPin(progressPin);

        if(provide < baseEfficiency) build.setPin(providePin, baseEfficiency);

        if(build.payload != null && contains(payloads, build.payload) && build.moveInPayload()){
            build.payloads.add(build.payload.content());
            build.payload = null;
        }

        //if efficiency has been used
        if(consumed > 0f && has(build, payloads)){
            build.setPin(providePin, baseEfficiency + efficiencyIncrease);
            progress += consumed * Time.delta;

            //consumption
            while(progress > time && has(build, payloads)){
                for(PayloadStack stack : payloads){
                    build.payloads.remove(stack.item, stack.amount);
                }
                progress -= time;
            }
            build.setPin(progressPin, progress);
        }
    }

    public static boolean contains(PayloadStack[] payloads, Payload pay){
        for(PayloadStack stack : payloads){
            if(stack.item == pay.content()) return true;
        }
        return false;
    }

    public static boolean has(ModularCrafterBuild build, PayloadStack[] payloadStacks){
        return Arrays.stream(payloadStacks).allMatch(stack -> build.payloads.contains(stack));
    }

    @Override
    public void setup(ModularCrafter block){
        block.acceptsPayload = true;
        block.acceptsUnitPayloads = true;
        for(PayloadStack stack : payloads){
            block.acceptedPayloads.add(stack.item);
        }
    }
}
