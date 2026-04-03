package meld.world.blocks.crafting.modules.rework;

import meld.world.blocks.crafting.*;
import meld.world.blocks.crafting.ModularCrafter.*;
import mindustry.type.*;
import mindustry.world.blocks.payloads.*;

import java.util.*;

public class ConsumePayloadModule extends ConsumeDiscreteModule{
    public PayloadStack[] payloads;

    public ConsumePayloadModule(int... outputPins){
        super(outputPins);
    }

    @Override
    public void update(ModularCrafterBuild build){
        super.update(build);

        if(build.payload != null && wants(build.payload) && build.moveInPayload()){
            build.payloads.add(build.payload.content());
            build.payload = null;
        }
    }

    @Override
    public boolean canConsume(ModularCrafterBuild build){
        return Arrays.stream(payloads).allMatch(stack -> build.payloads.contains(stack));
    }

    @Override
    public void consume(ModularCrafterBuild build){
        for(PayloadStack stack : payloads){
            build.payloads.remove(stack.item, stack.amount);
        }
    }

    public boolean wants(Payload pay){
        for(PayloadStack stack : payloads){
            if(stack.item == pay.content()) return true;
        }
        return false;
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
