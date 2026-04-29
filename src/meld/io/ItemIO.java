package meld.io;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.type.Item;
import mindustry.type.ItemStack;

//An IO class used to handle item-related input and output
public class ItemIO {
    public static void writeStack(Writes write, ItemStack stack){
        write.str(stack.item.name);
        write.i(stack.amount);
    }

    public static ItemStack readStack(Reads reads){
        return new ItemStack(Vars.content.item(reads.str()), reads.i());
    }
    public static void writeStacks(Writes write, ItemStack[] stacks){
        write.i(stacks.length);
        for (ItemStack stack: stacks) {
            writeStack(write, stack);
        }
    }

    public static ItemStack[] readStacks(Reads read){
        int size = read.i();
        ItemStack[] items = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            items[i] = readStack(read);
        }
        return items;
    }

    public static void writeItem(Writes write, Item item){
        write.str(item == null ? "" : item.name);
    }
    public static Item readItem(Reads reads){
        return Vars.content.item(reads.str());
    }
}