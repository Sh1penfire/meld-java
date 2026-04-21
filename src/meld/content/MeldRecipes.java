package meld.content;

import arc.struct.Seq;
import meld.fluid.AspectGroup;
import meld.world.blocks.consumers.StupidConsumeAspects;
import meld.world.blocks.crafting.recipe.TimedRecipe;
import meld.world.blocks.producer.ProduceItem;
import meld.world.blocks.producer.ProduceLiquid;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.consumers.ConsumeItems;

import static meld.content.MeldBlocks.outletRate;
import static mindustry.type.ItemStack.with;

public class MeldRecipes {
    public static Seq<TimedRecipe> kilnRecipies(float outlets, float speedMultiplier){
        return Seq.with(
                new TimedRecipe(120/speedMultiplier){{
                    consumers.addAll(
                            new ConsumeItems(with(MeldItems.tenbris, 2, MeldItems.carbolith, 2)),
                            new StupidConsumeAspects(outletRate * outlets, AspectGroup.aspect)
                    );
                    producers.addAll(
                            new ProduceItem(new ItemStack(MeldItems.shadesteel, 2)),
                            new ProduceLiquid(new LiquidStack(MeldLiquids.fumes, 1 * speedMultiplier))
                    );
                }},
                new TimedRecipe(30/speedMultiplier){{
                    consumers.addAll(
                            new ConsumeItems(with(MeldItems.silver, 2)),
                            new StupidConsumeAspects(outletRate * outlets, AspectGroup.aspect)
                    );
                    producers.addAll(
                            new ProduceItem(new ItemStack(MeldItems.annealedSilver, 3))
                    );
                }},
                new TimedRecipe(240/speedMultiplier){{
                    consumers.addAll(
                            new ConsumeItems(with(MeldItems.clayMallows, 2, MeldItems.likestoneSediments, 2, MeldItems.quartzStrata, 4)),
                            new StupidConsumeAspects(outletRate * outlets, AspectGroup.aspect)
                    );
                    producers.addAll(
                            new ProduceItem(new ItemStack(MeldItems.glassMallows, 4))
                    );
                }},
                new TimedRecipe(240/speedMultiplier){{
                    consumers.addAll(
                            new ConsumeItems(with(MeldItems.clayMallows, 4, MeldItems.likestoneSediments, 2, MeldItems.debris, 2)),
                            new StupidConsumeAspects(outletRate * outlets, AspectGroup.aspect)
                    );
                    producers.addAll(
                            new ProduceItem(new ItemStack(MeldItems.cruciblePlating, 4))
                    );
                }}
        );
    }
}
