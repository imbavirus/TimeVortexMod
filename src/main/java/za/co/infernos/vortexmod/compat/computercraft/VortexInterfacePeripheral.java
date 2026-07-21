package za.co.infernos.vortexmod.compat.computercraft;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.Nullable;
import za.co.infernos.vortexmod.block.entity.VortexInterfaceBlockEntity;

import java.util.Map;

/**
 * ComputerCraft peripheral exposing the Vortex Interface Lua API.
 */
public class VortexInterfacePeripheral implements IPeripheral {
    private final VortexInterfaceBlockEntity blockEntity;

    public VortexInterfacePeripheral(VortexInterfaceBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public String getType() {
        return "vortex_interface";
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return other instanceof VortexInterfacePeripheral peripheral
                && peripheral.blockEntity == this.blockEntity;
    }

    @Override
    public Object getTarget() {
        return this.blockEntity;
    }

    @LuaFunction(mainThread = true)
    public final boolean setSign(String signText) throws LuaException {
        return this.blockEntity.setSign(signText);
    }

    @LuaFunction
    public final Integer getPower() throws LuaException {
        return this.blockEntity.getPower();
    }

    @LuaFunction
    public final Boolean enableThrottle() throws LuaException {
        return this.blockEntity.enableThrottle();
    }

    @LuaFunction
    public final Boolean disableThrottle() throws LuaException {
        return this.blockEntity.disableThrottle();
    }

    @LuaFunction
    public final Boolean readyToLand() throws LuaException {
        return this.blockEntity.readyToLand();
    }

    @LuaFunction
    public final Boolean isFlying() throws LuaException {
        return this.blockEntity.isFlying();
    }

    @LuaFunction
    public final Boolean setCoords(String param) throws LuaException {
        return this.blockEntity.setCoords(param);
    }

    @LuaFunction
    public final Boolean setDimension(String param) throws LuaException {
        return this.blockEntity.setDimension(param);
    }

    @LuaFunction
    public final Map<String, Integer> getTargetLocation() throws LuaException {
        return this.blockEntity.getTargetLocation();
    }

    @LuaFunction
    public final Map<String, Integer> getExtLocation() throws LuaException {
        return this.blockEntity.getExtLocation();
    }

    @LuaFunction(mainThread = true)
    public final String getTargetDimension() throws LuaException {
        return this.blockEntity.getTargetDimension();
    }

    @LuaFunction(mainThread = true)
    public final String getExtDimension() throws LuaException {
        return this.blockEntity.getExtDimension();
    }

    @LuaFunction
    public final String getTargetRotation() throws LuaException {
        return this.blockEntity.getTargetRotation();
    }

    @LuaFunction(mainThread = true)
    public final String getTargetBlock() throws LuaException {
        return this.blockEntity.getTargetBlock();
    }

    @LuaFunction
    public final Integer getFlightTime() throws LuaException {
        return this.blockEntity.getFlightTime();
    }
}
