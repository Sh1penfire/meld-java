package meld.world.blocks.crafting.modules.rework;

import arc.*;
import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import meld.world.blocks.crafting.modules.rework.base.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

public class ProducePayloadModule extends ProduceDiscreteModule{
    public PayloadStack[] payloads;

    public ProducePayloadModule(int... inputPins){
        super(inputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        super.update(build);

        //dump payloads
        if(build.payload == null && build.payloads.any()){
            for(PayloadStack stack : payloads){
                if(build.payloads.contains(stack.item)){
                    build.payloads.remove(stack.item);

                    if(stack.item instanceof UnitType type){
                        build.payload = new UnitPayload(type.create(build.team));
                        Unit p = ((UnitPayload)build.payload).unit;
                        /*if(commandPos != null && p.isCommandable()){
                            p.command().commandPosition(commandPos);
                        }*/
                        Events.fire(new UnitCreateEvent(p, build));
                    }else if(stack.item instanceof Block block){
                        build.payload = new BuildPayload(block, build.team);
                    }
                    build.payVector.setZero();
                    build.payRotation = build.rotdeg();
                    break;
                }
            }
        }

        if(build.payload != null && shouldDump(build.payload)){
            build.moveOutPayload();
        }
    }

    @Override
    public boolean canOutput(ModularCrafterBuild build){
        return Arrays.stream(payloads).allMatch(stack -> build.payloads.get(stack.item) + stack.amount <= build.modular.payloadCapacity);
    }

    @Override
    public void output(ModularCrafterBuild build){
        for(PayloadStack stack : payloads){
            build.payloads.add(stack.item, stack.amount);
        }
    }

    public boolean shouldDump(Payload pay){
        for(PayloadStack stack : payloads){
            if(stack.item == pay.content()) return true;
        }
        return false;
    }

    @Override
    public void setup(ModularCrafter block){
        block.rotate = true;
        block.outputsPayload = true;
        for(PayloadStack stack : payloads){
            block.dumpedPayloads.add(stack.item);
        }
    }
}
