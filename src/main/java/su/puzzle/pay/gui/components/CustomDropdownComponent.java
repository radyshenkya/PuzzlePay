package su.puzzle.pay.gui.components;

import java.util.function.Consumer;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.DropdownComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.*;
import net.minecraft.text.Text;

public class CustomDropdownComponent extends FlowLayout {
    private static final String EXPANDED_DROPDOWN_CHAR = " ↑";
    private static final String UNEXPANDED_DROPDOWN_CHAR = " ↓";
	protected final int TITLE_OUTLINE_COLOR = 0xffa0a0a0;
    protected final int OPTIONS_OUTLINE_COLOR = 0xff666666;
    protected final int BG_COLOR = 0xff000000;
    protected final Insets PADDING = Insets.of(5);
    protected final BetterDropdownComponent expandableDropdown;
    protected final FlowLayout contentLayout;
    protected final BetterDropdownComponent titleDropdown;
    protected final Text title;
    protected final LabelComponent titleLabel;
    protected boolean expanded;

    public CustomDropdownComponent(Sizing horizontalSizing, Sizing verticalSizing, Text title, boolean expanded) {
        super(horizontalSizing, verticalSizing, Algorithm.HORIZONTAL);

        this.title = title;
        this.expanded = expanded;

        contentLayout = Containers.verticalFlow(horizontalSizing, verticalSizing);

        expandableDropdown = new BetterDropdownComponent(horizontalSizing);
        expandableDropdown.surface(Surface.flat(BG_COLOR));
        expandableDropdown.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        expandableDropdown.margins(Insets.top(-2));
        expandableDropdown.padding(Insets.of(0));

        titleDropdown = new BetterDropdownComponent(horizontalSizing);
        titleDropdown.surface(Surface.flat(BG_COLOR));
        titleDropdown.button(title, (comp) -> {
            this.expanded = !this.expanded;
            updateExpandableDropdown();
        });

        FlowLayout dropdownLayout = ((FlowLayout) titleDropdown.children().get(0));

        Component child = dropdownLayout.children().get(dropdownLayout.children().size() - 1);
        titleLabel = (LabelComponent) child;
        child.margins(PADDING);
        dropdownLayout.removeChild(child);

        FlowLayout layout = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        layout.child(child);
        child.horizontalSizing(Sizing.content());
        layout.surface(Surface.outline(TITLE_OUTLINE_COLOR));

        dropdownLayout.child(layout);

        contentLayout.child(titleDropdown);

        updateExpandableDropdown();

        super.child(contentLayout);
    }

    public CustomDropdownComponent button(Text text, Consumer<BetterDropdownComponent> onClick) {
        expandableDropdown.button(text, dropdown -> {
            this.expanded(false);
            onClick.accept(dropdown);
        });

        FlowLayout dropdownLayout = ((FlowLayout) expandableDropdown.children().get(0));

        Component child = dropdownLayout.children().get(dropdownLayout.children().size() - 1);
        child.margins(PADDING);
        dropdownLayout.removeChild(child);

        FlowLayout layout = Containers.verticalFlow(Sizing.content(), Sizing.content());
        layout.child(child);
        layout.margins(Insets.top(-1));
        child.horizontalSizing(Sizing.fill(100));
        layout.surface(Surface.outline(OPTIONS_OUTLINE_COLOR));

        dropdownLayout.child(layout);

        return this;
    }

    public boolean expanded() {
        return expanded;
    }

    public void expanded(boolean value) {
        expanded = value;
        updateExpandableDropdown();
    }

    protected void updateExpandableDropdown() {
        expandableDropdown.horizontalSizing(Sizing.fixed(titleDropdown.width()));

        if (!expanded) {
            titleLabel.text(Text.literal(title.getString() + UNEXPANDED_DROPDOWN_CHAR));
            contentLayout.removeChild(expandableDropdown);
        } else {
            titleLabel.text(Text.literal(title.getString() + EXPANDED_DROPDOWN_CHAR));
            contentLayout.child(expandableDropdown);
        }
    }
}
