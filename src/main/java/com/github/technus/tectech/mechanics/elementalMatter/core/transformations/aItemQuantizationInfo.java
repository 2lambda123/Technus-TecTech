package com.github.technus.tectech.mechanics.elementalMatter.core.transformations;

import com.github.technus.tectech.mechanics.elementalMatter.core.stacks.iElementalStack;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_OreDictUnificator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Tec on 23.05.2017.
 */
public class aItemQuantizationInfo implements iExchangeInfo<ItemStack, iElementalStack> {
    private final ItemStack       in;
    private final boolean         skipNBT;
    private final iElementalStack out;

    public aItemQuantizationInfo(ItemStack itemStackIn, boolean skipNBT, iElementalStack emOut) {
        in = itemStackIn;
        out = emOut;
        this.skipNBT = skipNBT;
    }

    public aItemQuantizationInfo(OrePrefixes prefix, Materials material, int amount, boolean skipNBT, iElementalStack emOut) {
        in = GT_OreDictUnificator.get(prefix, material, amount);
        out = emOut;
        this.skipNBT = skipNBT;
    }

    @Override
    public ItemStack input() {
        return in.copy();
    }

    @Override
    public iElementalStack output() {
        return out.clone();
    }

    @Override
    public int hashCode() {
        return (GameRegistry.findUniqueIdentifierFor(in.getItem())+":"+in.getUnlocalizedName()+ ':' +in.getItemDamage()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  aItemQuantizationInfo){
            //alias
            ItemStack stack=((aItemQuantizationInfo) obj).in;
            if(!in.getUnlocalizedName().equals(((aItemQuantizationInfo) obj).in.getUnlocalizedName())) {
                return false;
            }

            if(!GameRegistry.findUniqueIdentifierFor(in.getItem()).equals(
                GameRegistry.findUniqueIdentifierFor(((aItemQuantizationInfo) obj).in.getItem()))) {
                return false;
            }

            if(in.getItemDamage() != OreDictionary.WILDCARD_VALUE && stack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
                if (in.getItemDamage() != stack.getItemDamage()) {
                    return false;
                }
            }
            return skipNBT || ItemStack.areItemStackTagsEqual(in, stack);
        }
        return false;
    }
}
