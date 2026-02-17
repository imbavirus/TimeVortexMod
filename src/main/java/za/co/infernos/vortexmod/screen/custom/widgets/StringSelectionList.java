package za.co.infernos.vortexmod.screen.custom.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * A scrollable list widget for selecting a string from a list.
 * Used for dimension selection and location selection in the Keypad GUI.
 */
public class StringSelectionList extends ObjectSelectionList<StringSelectionList.Entry> {
    private final Consumer<String> onSelect;
    private String selectedValue = null;
    private boolean suppressCallback = false;

    public StringSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight, Consumer<String> onSelect) {
        super(minecraft, width, height, y, itemHeight);
        this.onSelect = onSelect;
        this.setRenderHeader(false, 0);
    }

    public void updateEntries(List<String> values) {
        this.clearEntries();
        for (String value : values) {
            this.addEntry(new Entry(value));
        }
        // Re-select the previously selected value if still present
        if (selectedValue != null) {
            suppressCallback = true;
            for (Entry entry : this.children()) {
                if (entry.value.equals(selectedValue)) {
                    this.setSelected(entry);
                    break;
                }
            }
            suppressCallback = false;
        }
    }

    public void updateEntries(List<String> values, String filter) {
        this.clearEntries();
        String lowerFilter = filter.toLowerCase();
        for (String value : values) {
            if (filter.isEmpty() || value.toLowerCase().contains(lowerFilter)) {
                this.addEntry(new Entry(value));
            }
        }
        // Re-select the previously selected value if still present
        if (selectedValue != null) {
            suppressCallback = true;
            for (Entry entry : this.children()) {
                if (entry.value.equals(selectedValue)) {
                    this.setSelected(entry);
                    break;
                }
            }
            suppressCallback = false;
        }
    }

    public String getSelectedValue() {
        Entry selected = this.getSelected();
        return selected != null ? selected.value : null;
    }

    public void setSelectedValue(String value) {
        this.selectedValue = value;
        for (Entry entry : this.children()) {
            if (entry.value.equals(value)) {
                this.setSelected(entry);
                return;
            }
        }
    }

    @Override
    public void setSelected(Entry entry) {
        super.setSelected(entry);
        if (entry != null) {
            this.selectedValue = entry.value;
            if (onSelect != null && !suppressCallback) {
                onSelect.accept(entry.value);
            }
        }
    }

    @Override
    public int getRowWidth() {
        return this.width - 12;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getX() + this.width - 6;
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        public final String value;
        private final Component displayText;

        public Entry(String value) {
            this.value = value;
            // Prettify dimension names (e.g., "minecraft:overworld" -> "overworld")
            String displayName = value;
            if (value.contains(":")) {
                displayName = value.substring(value.lastIndexOf(':') + 1);
            }
            this.displayText = Component.literal(displayName);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            boolean isSelected = StringSelectionList.this.getSelected() == this;
            int textColor = isSelected ? 0x00FFFF : (isMouseOver ? 0xFFFFFF : 0xAAAAAA);
            graphics.drawString(StringSelectionList.this.minecraft.font, displayText, left + 2, top + 2, textColor, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                StringSelectionList.this.setSelected(this);
                return true;
            }
            return false;
        }

        @Override
        public Component getNarration() {
            return Component.literal(value);
        }
    }
}
