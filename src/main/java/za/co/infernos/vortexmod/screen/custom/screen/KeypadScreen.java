package za.co.infernos.vortexmod.screen.custom.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import za.co.infernos.vortexmod.VortexMod;
import za.co.infernos.vortexmod.network.PacketHandler;
import za.co.infernos.vortexmod.network.ServerboundDeleteTargetPacket;
import za.co.infernos.vortexmod.network.ServerboundSaveTargetPacket;
import za.co.infernos.vortexmod.network.ServerboundTargetPacket;
import za.co.infernos.vortexmod.screen.custom.menu.KeypadMenu;
import za.co.infernos.vortexmod.screen.custom.widgets.CustomButton;
import za.co.infernos.vortexmod.screen.custom.widgets.StringSelectionList;

import java.util.*;

public class KeypadScreen extends AbstractContainerScreen<KeypadMenu> {
    private Boolean targetScreen = true;

    // Coordinate input widgets
    private EditBox xField;
    private EditBox yField;
    private EditBox zField;
    private EditBox rotationField;
    private EditBox dimensionFilterField;
    
    // Dimension selection list
    private StringSelectionList dimensionList;
    private String selectedDimension = "";

    // Buttons
    private CustomButton setButton;
    private CustomButton cancelButton;
    private CustomButton toggleButton;

    // Location screen widgets
    private EditBox nameField;
    private StringSelectionList locationList;
    private CustomButton saveButton;
    private CustomButton loadButton;
    private CustomButton deleteButton;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/gui/keypad_gui.png");
    private static final ResourceLocation TEXTURE_SECONDARY =
            ResourceLocation.fromNamespaceAndPath(VortexMod.MODID, "textures/gui/keypad_gui_locations.png");

    // Expanded GUI dimensions
    private static final int GUI_WIDTH = 220;
    private static final int GUI_HEIGHT = 180;

    public KeypadScreen(KeypadMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        this.titleLabelX = 10000;

        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;

        // ===== Target Screen Widgets =====
        // X coordinate
        this.xField = new EditBox(this.font, guiLeft + 30, guiTop + 20, 40, 14, Component.literal("X"));
        this.xField.setMaxLength(8);
        this.xField.setTextColor(0xFFFFFF);
        this.xField.setFilter(this::isValidCoordinateInput);
        this.addWidget(this.xField);

        // Y coordinate
        this.yField = new EditBox(this.font, guiLeft + 90, guiTop + 20, 40, 14, Component.literal("Y"));
        this.yField.setMaxLength(8);
        this.yField.setTextColor(0xFFFFFF);
        this.yField.setFilter(this::isValidCoordinateInput);
        this.addWidget(this.yField);

        // Z coordinate
        this.zField = new EditBox(this.font, guiLeft + 150, guiTop + 20, 40, 14, Component.literal("Z"));
        this.zField.setMaxLength(8);
        this.zField.setTextColor(0xFFFFFF);
        this.zField.setFilter(this::isValidCoordinateInput);
        this.addWidget(this.zField);

        // Rotation
        this.rotationField = new EditBox(this.font, guiLeft + 35, guiTop + 40, 35, 14, Component.literal("Rot"));
        this.rotationField.setMaxLength(3);
        this.rotationField.setTextColor(0xFFFFFF);
        this.rotationField.setFilter(this::isValidRotationInput);
        this.addWidget(this.rotationField);

        // Dimension filter
        this.dimensionFilterField = new EditBox(this.font, guiLeft + 100, guiTop + 40, 100, 14, Component.literal("Filter"));
        this.dimensionFilterField.setMaxLength(32);
        this.dimensionFilterField.setTextColor(0xFFFFFF);
        this.dimensionFilterField.setHint(Component.literal("Filter dimensions...").withStyle(ChatFormatting.DARK_GRAY));
        this.dimensionFilterField.setResponder(this::onDimensionFilterChanged);
        this.addWidget(this.dimensionFilterField);

        // Dimension list
        this.dimensionList = new StringSelectionList(
                this.minecraft, 
                200, 
                70, 
                guiTop + 58, 
                12, 
                this::onDimensionSelected
        );
        this.dimensionList.setX(guiLeft + 10);
        this.addWidget(this.dimensionList);
        updateDimensionList();

        // Set button
        this.setButton = this.addRenderableWidget(CustomButton.builder(Component.literal("Set Target"), (btn) -> {
            this.onDone();
        }).bounds(guiLeft + 10, guiTop + 145, 65, 20).build());

        // Cancel button
        this.cancelButton = this.addRenderableWidget(CustomButton.builder(CommonComponents.GUI_CANCEL, (btn) -> {
            this.onClose();
        }).bounds(guiLeft + 80, guiTop + 145, 60, 20).build());

        // Toggle button
        this.toggleButton = this.addRenderableWidget(CustomButton.builder(Component.literal("Locations"), (btn) -> {
            this.targetScreen = !this.targetScreen;
            this.toggleButton.setMessage(Component.literal(this.targetScreen ? "Locations" : "Coords"));
        }).bounds(guiLeft + 145, guiTop + 145, 65, 20).build());
        this.toggleButton.setTooltip(Tooltip.create(Component.literal("Toggle between coordinates and saved locations")));

        // ===== Locations Screen Widgets =====
        // Name/filter field
        this.nameField = new EditBox(this.font, guiLeft + 45, guiTop + 20, 155, 14, Component.literal("Name"));
        this.nameField.setMaxLength(32);
        this.nameField.setTextColor(0xFFFFFF);
        this.nameField.setHint(Component.literal("Location name...").withStyle(ChatFormatting.DARK_GRAY));
        this.nameField.setResponder(this::onLocationFilterChanged);
        this.addWidget(this.nameField);

        // Location list
        this.locationList = new StringSelectionList(
                this.minecraft,
                200,
                85,
                guiTop + 38,
                12,
                this::onLocationSelected
        );
        this.locationList.setX(guiLeft + 10);
        this.addWidget(this.locationList);

        // Save button
        this.saveButton = this.addRenderableWidget(CustomButton.builder(Component.literal("Save"), (btn) -> {
            this.onSave();
        }).bounds(guiLeft + 5, guiTop + 145, 42, 20).build());

        // Load button
        this.loadButton = this.addRenderableWidget(CustomButton.builder(Component.literal("Load"), (btn) -> {
            this.onLoad();
        }).bounds(guiLeft + 52, guiTop + 145, 42, 20).build());

        // Delete button
        this.deleteButton = this.addRenderableWidget(CustomButton.builder(Component.literal("Delete"), (btn) -> {
            this.onDelete();
        }).bounds(guiLeft + 99, guiTop + 145, 42, 20).build());

        this.setInitialFocus(this.xField);
    }

    private void onDimensionFilterChanged(String filter) {
        updateDimensionList();
    }

    private void onDimensionSelected(String dimension) {
        this.selectedDimension = dimension;
    }

    private void onLocationFilterChanged(String filter) {
        updateLocationList();
    }

    private void onLocationSelected(String location) {
        // Parse the location string to extract just the name
        int bracketIdx = location.indexOf('(');
        if (bracketIdx > 0) {
            this.nameField.setValue(location.substring(0, bracketIdx).trim());
        } else {
            this.nameField.setValue(location);
        }
    }

    private void updateDimensionList() {
        List<String> levels = new ArrayList<>(this.menu.blockEntity.serverLevels);
        String filter = this.dimensionFilterField != null ? this.dimensionFilterField.getValue() : "";
        this.dimensionList.updateEntries(levels, filter);
    }

    private void updateLocationList() {
        Set<String> coordKeys = this.menu.blockEntity.coordData.keySet();
        List<String> locationStrings = new ArrayList<>();
        String playerName = this.minecraft.player.getScoreboardName();
        String filter = this.nameField != null ? this.nameField.getValue() : "";

        for (String coordKey : coordKeys) {
            if (coordKey.startsWith(playerName)) {
                String pointName = coordKey.substring(playerName.length());
                BlockPos pointPos = this.menu.blockEntity.coordData.get(coordKey);
                String pointDimension = this.menu.blockEntity.dimData.get(coordKey);
                String locString = pointName + " (" + pointPos.getX() + " " + pointPos.getY() + " " + pointPos.getZ() + ") | " + pointDimension;
                
                if (filter.isEmpty() || locString.toLowerCase().contains(filter.toLowerCase())) {
                    locationStrings.add(locString);
                }
            }
        }
        this.locationList.updateEntries(locationStrings);
    }

    public void onSave() {
        if (!this.targetScreen && !this.nameField.getValue().isEmpty()) {
            PacketHandler.sendToServer(new ServerboundSaveTargetPacket(
                    this.menu.blockEntity.getBlockPos(), 
                    this.nameField.getValue(), 
                    true, 
                    this.targetScreen
            ));
        }
    }

    public void onLoad() {
        if (!this.targetScreen && !this.nameField.getValue().isEmpty()) {
            PacketHandler.sendToServer(new ServerboundSaveTargetPacket(
                    this.menu.blockEntity.getBlockPos(), 
                    this.nameField.getValue(), 
                    false, 
                    this.targetScreen
            ));
        }
    }

    public void onDelete() {
        if (!this.targetScreen && !this.nameField.getValue().isEmpty()) {
            PacketHandler.sendToServer(new ServerboundDeleteTargetPacket(
                    this.menu.blockEntity.getBlockPos(), 
                    this.nameField.getValue(), 
                    this.targetScreen
            ));
        }
    }

    public void onDone() {
        if (this.targetScreen) {
            int xInt = parseIntOrDefault(this.xField.getValue(), 999999999);
            int yInt = parseIntOrDefault(this.yField.getValue(), 999999999);
            int zInt = parseIntOrDefault(this.zField.getValue(), 999999999);
            int rotInt = parseIntOrDefault(this.rotationField.getValue(), 999999999);

            BlockPos fromPos = this.menu.blockEntity.getBlockPos();
            String fromDimension = this.menu.blockEntity.getLevel().dimension().location().getPath();
            BlockPos toPos = new BlockPos(xInt, yInt, zInt);
            String toDimension = this.selectedDimension;

            if (toDimension.equals("tardisdim")) {
                this.minecraft.player.displayClientMessage(
                        Component.literal("You cannot set the dimension to the TARDIS dimension.").withStyle(ChatFormatting.RED), 
                        false
                );
            } else if (!toDimension.isEmpty()) {
                PacketHandler.sendToServer(new ServerboundTargetPacket(fromPos, fromDimension, toPos, rotInt, toDimension, this.targetScreen));
            } else {
                this.minecraft.player.displayClientMessage(
                        Component.literal("Please select a dimension.").withStyle(ChatFormatting.YELLOW), 
                        false
                );
            }
        }
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value.matches("^-?\\d+$")) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    private boolean isValidCoordinateInput(String input) {
        if (input.isEmpty()) return true;
        if (input.equals("-")) return true;
        return input.matches("^-?\\d+$");
    }

    private boolean isValidRotationInput(String input) {
        if (input.isEmpty()) return true;
        return input.matches("^\\d+$");
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // Escape
            this.minecraft.player.closeContainer();
            return true;
        }

        // Let edit boxes consume input and prevent 'E' from closing the screen
        if (this.xField.isFocused() || this.yField.isFocused() || this.zField.isFocused() 
                || this.rotationField.isFocused() || this.dimensionFilterField.isFocused() 
                || this.nameField.isFocused()) {
            if (this.minecraft.options.keyInventory.isActiveAndMatches(com.mojang.blaze3d.platform.InputConstants.getKey(keyCode, scanCode))) {
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;

        // Draw a dark background panel
        guiGraphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, 0xCC1E1E2E);
        // Border
        guiGraphics.renderOutline(guiLeft, guiTop, GUI_WIDTH, GUI_HEIGHT, 0xFF45475A);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);

        int guiLeft = (this.width - GUI_WIDTH) / 2;
        int guiTop = (this.height - GUI_HEIGHT) / 2;

        // Update widget visibility based on screen mode
        updateWidgetVisibility();

        if (this.targetScreen) {
            // Title
            guiGraphics.drawCenteredString(this.font, "Set Destination", guiLeft + GUI_WIDTH / 2, guiTop + 5, 0x00FFFF);

            // Labels
            guiGraphics.drawString(this.font, "X:", guiLeft + 15, guiTop + 23, 0xAAAAAA);
            guiGraphics.drawString(this.font, "Y:", guiLeft + 75, guiTop + 23, 0xAAAAAA);
            guiGraphics.drawString(this.font, "Z:", guiLeft + 135, guiTop + 23, 0xAAAAAA);
            guiGraphics.drawString(this.font, "Rot:", guiLeft + 10, guiTop + 43, 0xAAAAAA);
            guiGraphics.drawString(this.font, "Dim:", guiLeft + 75, guiTop + 43, 0xAAAAAA);

            // Render edit boxes
            this.xField.render(guiGraphics, mouseX, mouseY, delta);
            this.yField.render(guiGraphics, mouseX, mouseY, delta);
            this.zField.render(guiGraphics, mouseX, mouseY, delta);
            this.rotationField.render(guiGraphics, mouseX, mouseY, delta);
            this.dimensionFilterField.render(guiGraphics, mouseX, mouseY, delta);

            // Render dimension list
            this.dimensionList.render(guiGraphics, mouseX, mouseY, delta);

            // Show selected dimension
            String selectedText = this.selectedDimension.isEmpty() ? "None selected" : this.selectedDimension;
            guiGraphics.drawString(this.font, "Selected: " + selectedText, guiLeft + 15, guiTop + 132, 0x88FF88);
        } else {
            // Title
            guiGraphics.drawCenteredString(this.font, "Saved Locations", guiLeft + GUI_WIDTH / 2, guiTop + 5, 0x00FFFF);

            // Name field label
            guiGraphics.drawString(this.font, "Name:", guiLeft + 10, guiTop + 23, 0xAAAAAA);

            // Render name field
            this.nameField.render(guiGraphics, mouseX, mouseY, delta);

            // Render location list
            updateLocationList();
            this.locationList.render(guiGraphics, mouseX, mouseY, delta);
        }

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void updateWidgetVisibility() {
        // Target screen widgets
        this.xField.visible = this.targetScreen;
        this.yField.visible = this.targetScreen;
        this.zField.visible = this.targetScreen;
        this.rotationField.visible = this.targetScreen;
        this.dimensionFilterField.visible = this.targetScreen;
        this.setButton.visible = this.targetScreen;
        this.cancelButton.visible = this.targetScreen;

        // Location screen widgets
        this.nameField.visible = !this.targetScreen;
        this.saveButton.visible = !this.targetScreen;
        this.loadButton.visible = !this.targetScreen;
        this.deleteButton.visible = !this.targetScreen;

        // Toggle button always visible
        this.toggleButton.visible = true;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String xVal = this.xField.getValue();
        String yVal = this.yField.getValue();
        String zVal = this.zField.getValue();
        String rotVal = this.rotationField.getValue();
        String dimFilterVal = this.dimensionFilterField.getValue();
        String nameVal = this.nameField.getValue();
        String selectedDim = this.selectedDimension;

        this.init(minecraft, width, height);

        this.xField.setValue(xVal);
        this.yField.setValue(yVal);
        this.zField.setValue(zVal);
        this.rotationField.setValue(rotVal);
        this.dimensionFilterField.setValue(dimFilterVal);
        this.nameField.setValue(nameVal);
        this.selectedDimension = selectedDim;
        updateDimensionList();
        if (!selectedDim.isEmpty()) {
            this.dimensionList.setSelectedValue(selectedDim);
        }
    }
}