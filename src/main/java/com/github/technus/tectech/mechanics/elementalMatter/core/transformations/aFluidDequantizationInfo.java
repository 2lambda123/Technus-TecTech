package com.github.technus.tectech.mechanics.elementalMatter.core.transformations;

import com.github.technus.tectech.mechanics.elementalMatter.core.stacks.iElementalStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Tec on 23.05.2017.
 */
public class aFluidDequantizationInfo implements iExchangeInfo<iElementalStack,FluidStack> {
    private final iElementalStack in;
    private final FluidStack      out;

    public aFluidDequantizationInfo(iElementalStack emIn, FluidStack fluidStackOut){
        in=emIn;
        out=fluidStackOut;
    }

    public aFluidDequantizationInfo(iElementalStack emIn, Fluid fluid, int fluidAmount){
        in=emIn;
        out=new FluidStack(fluid,fluidAmount);
    }

    @Override
    public iElementalStack input() {
        return in.clone();//MEH!
    }

    @Override
    public FluidStack output() {
        return out.copy();
    }

    @Override
    public int hashCode() {
        return in.getDefinition().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof aFluidDequantizationInfo && hashCode() == obj.hashCode();
    }
}
