package meld.world.blocks.io;

import arc.struct.ObjectFloatMap;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.type.Item;

public class BlockIO {

    public static void writeItemMap(ObjectFloatMap<Item> itemValues, Writes write){
        write.i(itemValues.size);
        for (ObjectFloatMap.Entry<Item> itemValue : itemValues) {
            write.str(itemValue.key.name);
            write.f(itemValue.value);
        }
    }

    public static ObjectFloatMap<Item> readItemMap(Reads reads){
        int size = reads.i();
        ObjectFloatMap<Item> output = new ObjectFloatMap<>();
        for(int i = 0; i < size; i++){
            Item item = Vars.content.item(reads.str());
            float value = reads.f();
            if(item != null) output.put(item, value);
        }
        return output;
    }
}
