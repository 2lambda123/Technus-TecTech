package com.github.technus.tectech.mechanics.elementalMatter.core.templates;

import com.github.technus.tectech.TecTech;
import com.github.technus.tectech.mechanics.elementalMatter.core.decay.EMDecay;
import com.github.technus.tectech.mechanics.elementalMatter.core.maps.EMConstantStackMap;
import com.github.technus.tectech.mechanics.elementalMatter.core.EMException;
import com.github.technus.tectech.mechanics.elementalMatter.core.transformations.EMFluidDequantizationInfo;
import com.github.technus.tectech.mechanics.elementalMatter.core.transformations.EMItemDequantizationInfo;
import com.github.technus.tectech.mechanics.elementalMatter.core.transformations.EMOredictDequantizationInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

import static com.github.technus.tectech.util.Util.areBitsSet;
import static com.github.technus.tectech.loader.TecTechConfig.DEBUG_MODE;
import static com.github.technus.tectech.mechanics.elementalMatter.definitions.primitive.EMPrimitiveDefinition.null__;
import static com.github.technus.tectech.thing.item.DebugElementalInstanceContainer_EM.STACKS_REGISTERED;
import static com.github.technus.tectech.thing.metaTileEntity.multi.GT_MetaTileEntity_EM_scanner.*;

/**
 * Created by danie_000 on 22.10.2016.
 * EXTEND THIS TO ADD NEW PRIMITIVES, WATCH OUT FOR ID'S!!!  (-1 to 32 can be assumed as used)
 */
public abstract class EMPrimitive extends EMComplex {
    public static final byte nbtType = (byte) 'p';

    private static final Map<Integer, EMPrimitive> bindsBO = new HashMap<>();

    public static Map<Integer, EMPrimitive> getBindsPrimitive() {
        return bindsBO;
    }

    private final String name;
    private final String symbol;
    //float-mass in eV/c^2
    private final double mass;
    //int -electric charge in 1/3rds of electron charge for optimization
    private final int    charge;
    //byte color; 0=Red 1=Green 2=Blue 0=Cyan 1=Magenta 2=Yellow, else ignored (-1 - uncolorable)
    private final byte   color;
    //-1/-2/-3 anti matter generations, +1/+2/+3 matter generations, 0 self anti
    private final byte type;

    private EMPrimitive anti;//IMMUTABLE
    private EMDecay[]   elementalDecays;
    private byte                naturalDecayInstant;
    private byte energeticDecayInstant;
    private double rawLifeTime;

    private final int ID;

    //no _ at end - normal particle
    //   _ at end - anti particle
    //  __ at end - self is antiparticle

    protected EMPrimitive(String name, String symbol, int type, double mass, int charge, int color, int ID) {
        this.name = name;
        this.symbol = symbol;
        this.type = (byte) type;
        this.mass = mass;
        this.charge = charge;
        this.color = (byte) color;
        this.ID = ID;
        if (bindsBO.put(ID, this) != null) {
            Minecraft.getMinecraft().crashed(new CrashReport("Primitive definition", new EMException("Duplicate ID")));
        }
        STACKS_REGISTERED.add(this);
    }

    //
    protected void init(EMPrimitive antiParticle, double rawLifeTime, int naturalInstant, int energeticInstant, EMDecay... elementalDecaysArray) {
        anti = antiParticle;
        this.rawLifeTime = rawLifeTime;
        naturalDecayInstant = (byte) naturalInstant;
        energeticDecayInstant = (byte) energeticInstant;
        elementalDecays =elementalDecaysArray;
    }

    @Override
    public String getLocalizedName() {
        return "Undefined: " + getName();
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getShortSymbol() {
        return getSymbol();
    }

    @Override
    public IEMDefinition getAnti() {
        return anti;//no need for copy
    }

    @Override
    public int getCharge() {
        return charge;
    }

    @Override
    public byte getColor() {
        return color;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public EMDecay[] getNaturalDecayInstant() {
        if (naturalDecayInstant < 0) {
            return elementalDecays;
        }else if (naturalDecayInstant>=elementalDecays.length){
            return EMDecay.NO_PRODUCT;
        }
        return new EMDecay[]{elementalDecays[naturalDecayInstant]};
    }

    @Override
    public EMDecay[] getEnergyInducedDecay(long energyLevel) {
        if (energeticDecayInstant < 0) {
            return elementalDecays;
        }else if (energeticDecayInstant>=elementalDecays.length){
            return EMDecay.NO_PRODUCT;
        }
        return new EMDecay[]{elementalDecays[energeticDecayInstant]};
    }

    @Override
    public double getEnergyDiffBetweenStates(long currentEnergyLevel, long newEnergyLevel) {
        return IEMDefinition.DEFAULT_ENERGY_REQUIREMENT *(newEnergyLevel-currentEnergyLevel);
    }

    @Override
    public boolean usesSpecialEnergeticDecayHandling() {
        return false;
    }

    @Override
    public boolean usesMultipleDecayCalls(long energyLevel) {
        return false;
    }

    @Override
    public boolean decayMakesEnergy(long energyLevel) {
        return false;
    }

    @Override
    public boolean fusionMakesEnergy(long energyLevel) {
        return false;
    }

    @Override
    public EMDecay[] getDecayArray() {
        return elementalDecays;
    }

    @Override
    public double getRawTimeSpan(long currentEnergy) {
        return rawLifeTime;
    }

    @Override
    public final EMConstantStackMap getSubParticles() {
        return null;
    }

    @Override
    public EMFluidDequantizationInfo someAmountIntoFluidStack() {
        return null;
    }

    @Override
    public EMItemDequantizationInfo someAmountIntoItemsStack() {
        return null;
    }

    @Override
    public EMOredictDequantizationInfo someAmountIntoOredictStack() {
        return null;
    }

    @Override
    public byte getType() {
        return type;
    }

    @Override
    public final NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("t", nbtType);
        nbt.setInteger("c", getID());
        return nbt;
    }

    public static EMPrimitive fromNBT(NBTTagCompound content) {
        EMPrimitive primitive = bindsBO.get(content.getInteger("c"));
        return primitive == null ? null__ : primitive;
    }

    @Override
    public final byte getClassType() {
        return -128;
    }

    public static byte getClassTypeStatic(){
        return -128;
    }

    @Override
    public void addScanShortSymbols(ArrayList<String> lines, int capabilities, long energyLevel) {
        if(areBitsSet(SCAN_GET_NOMENCLATURE|SCAN_GET_CHARGE|SCAN_GET_MASS|SCAN_GET_TIMESPAN_INFO, capabilities)) {
            lines.add(getShortSymbol());
        }
    }

    @Override
    public void addScanResults(ArrayList<String> lines, int capabilities, long energyLevel) {
        if(areBitsSet(SCAN_GET_CLASS_TYPE, capabilities)) {
            lines.add("CLASS = " + nbtType + ' ' + getClassType());
        }
        if(areBitsSet(SCAN_GET_NOMENCLATURE|SCAN_GET_CHARGE|SCAN_GET_MASS|SCAN_GET_TIMESPAN_INFO, capabilities)) {
            lines.add("NAME = "+ getLocalizedName());
            lines.add("SYMBOL = "+getSymbol());
        }
        if(areBitsSet(SCAN_GET_CHARGE,capabilities)) {
            lines.add("CHARGE = " + getCharge() / 3D + " e");
        }
        if(areBitsSet(SCAN_GET_COLOR,capabilities)) {
            lines.add(getColor() < 0 ? "COLORLESS" : "CARRIES COLOR");
        }
        if(areBitsSet(SCAN_GET_MASS,capabilities)) {
            lines.add("MASS = " + getMass() + " eV/c\u00b2");
        }
        if(areBitsSet(SCAN_GET_TIMESPAN_INFO, capabilities)){
            lines.add((isTimeSpanHalfLife()?"HALF LIFE = ":"LIFE TIME = ")+getRawTimeSpan(energyLevel)+ " s");
            lines.add("    "+"At current energy level");
        }
    }

    public static void run() {
        try {
            EMComplex.addCreatorFromNBT(nbtType, EMPrimitive.class.getMethod("fromNBT", NBTTagCompound.class),(byte)-128);
        } catch (Exception e) {
            if (DEBUG_MODE) {
                e.printStackTrace();
            }
        }
        if(DEBUG_MODE) {
            TecTech.LOGGER.info("Registered Elemental Matter Class: Primitive " + nbtType + ' ' + -128);
        }
    }

    @Override
    public final int compareTo(IEMDefinition o) {
        if (getClassType() == o.getClassType()) {
            int oID = ((EMPrimitive) o).getID();
            return Integer.compare(getID(), oID);
        }
        return compareClassID(o);
    }

    @Override
    public final int hashCode() {
        return getID();
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }
}