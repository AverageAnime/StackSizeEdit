package net.averageanime.stacksizeedit.mixin;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$8")
public class DispenserBehaviorMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();
        ItemStack emptyBucketStack = new ItemStack(Items.BUCKET);
        FluidModificationItem fluidModificationItem = (FluidModificationItem) stack.getItem();
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        World world = pointer.world();

        if (fluidModificationItem.placeFluid( null, world, blockPos, null)) {
            fluidModificationItem.onEmptied( null, world, stack, blockPos);
            if (stack.getCount() > 1) {
                ItemStack newStack = stack.copy();
                newStack.decrement(1);
                if (((DispenserBlockEntity)pointer.blockEntity()).addToFirstFreeSlot(emptyBucketStack.copy()) < 0) {
                    fallbackBehavior.dispense(pointer, emptyBucketStack.copy());
                }
                return newStack;
            } else {
                return new ItemStack(Items.BUCKET);
            }
        } else {
            return fallbackBehavior.dispense(pointer, stack);
        }
    }
}
