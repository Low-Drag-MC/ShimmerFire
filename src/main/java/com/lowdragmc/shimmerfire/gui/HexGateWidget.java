package com.lowdragmc.shimmerfire.gui;

import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.shimmerfire.WorldData;
import com.lowdragmc.shimmerfire.blockentity.multiblocked.HexGateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author KilaBash
 * @date 2022/6/28
 * @implNote HexGateWidget
 */
public class HexGateWidget extends WidgetGroup {
    private final HexGateBlockEntity hexGate;
    private final Map<BlockPos, String> gates = new HashMap<>();
    private final DraggableScrollableWidgetGroup gateGroup;
    private BlockPos selected;
    private String gateName;

    public HexGateWidget(HexGateBlockEntity hexGate) {
        super(0, 0, 200, 225);
        this.hexGate = hexGate;
        this.gateName = hexGate.gateName;
        this.setBackground(ResourceBorderTexture.BORDERED_BACKGROUND);
        this.addWidget(new ImageWidget(5, 5, 190, 20, new TextTexture("HexGate")));
        this.addWidget(new ImageWidget(5, 25, 190, 150, ResourceBorderTexture.BORDERED_BACKGROUND_BLUE));
        this.addWidget(new LabelWidget(5, 183, "Gate Name: "));
        this.addWidget(new TextFieldWidget(65, 180, 130, 15, null, s -> gateName = s).setCurrentString(gateName));
        this.addWidget(new ButtonWidget(5, 200, 190, 15, null, this::save).setButtonTexture(ResourceBorderTexture.BUTTON_COMMON, new TextTexture("save", -1).setDropShadow(true)).setHoverBorderTexture(1, -1));
        this.addWidget(gateGroup = new DraggableScrollableWidgetGroup(10, 30, 180, 140));
    }

    @Override
    public void writeInitialData(FriendlyByteBuf buffer) {
        super.writeInitialData(buffer);
        gates.putAll(WorldData.getOrCreate(hexGate.getLevel()).LOADED_HEX_GATE);
        buffer.writeVarInt(gates.size());
        gates.forEach(((pos, name) -> {
            buffer.writeBlockPos(pos);
            buffer.writeUtf(name);
        }));
        updateList();
    }

    @Override
    public void readInitialData(FriendlyByteBuf buffer) {
        super.readInitialData(buffer);
        for (int i = buffer.readVarInt(); i > 0; i--) {
            gates.put(buffer.readBlockPos(), buffer.readUtf());
        }
        updateList();
    }

    private void updateList() {
        gateGroup.clearAllWidgets();
        selected = null;
        int width = gateGroup.getSize().width;
        BlockPos init = hexGate.destination;
        AtomicBoolean first = new AtomicBoolean(true);
        AtomicReference<SelectableWidgetGroup> initSelectable = new AtomicReference<>(null);
        gates.forEach((pos, name) -> {
            SelectableWidgetGroup selectable = new SelectableWidgetGroup(0, 1 + gateGroup.widgets.size() * 11, width, 10);
            selectable.setSelectedTexture(-1, -1)
                    .setOnSelected(W -> {
                        if (first.get() && initSelectable.get() != null) {
                            first.set(false);
                            if (selectable != initSelectable.get()) {
                                initSelectable.get().onUnSelected();
                            }
                        }
                        selected = pos;
                        writeClientAction(13, buf -> buf.writeBlockPos(selected));
                    })
                    .addWidget(new ImageWidget(0, 0, width, 10, new ColorRectTexture(0x5f000000)))
                    .addWidget(new ImageWidget(0, 0, width, 10,
                            new TextTexture(name + " (%s)".formatted(pos.toShortString()))
                                    .setWidth(width).setType(TextTexture.TextType.ROLL)))
                    .addWidget(new ButtonWidget(width - 22, 0, 20, 10, new GuiTextureGroup(new ColorRectTexture(0xafff4444), new TextTexture("GO")), cd -> {
                        if (!cd.isRemote) {
                            hexGate.go(pos);
                            gui.entityPlayer.closeContainer();
                        }
                    })
                            .setHoverBorderTexture(1, -1));
            gateGroup.addWidget(selectable);

            if (pos.equals(init)) {
                initSelectable.set(selectable);
                if (isRemote()) {
                    selectable.onSelected();
                }
                first.set(true);
                selected = pos;
            }
        });
        if (selected != init) {
            hexGate.setGateInfo(gateName, selected);
        }
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        if (id == 13) {
            selected = buffer.readBlockPos();
        } else {
            super.handleClientAction(id, buffer);
        }
    }

    private void save(ClickData clickData) {
        if (!clickData.isRemote) {
            hexGate.setGateInfo(gateName, selected);
        }
    }

}
