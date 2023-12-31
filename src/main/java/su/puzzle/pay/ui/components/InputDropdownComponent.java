package su.puzzle.pay.ui.components;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.component.TextBoxComponent.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.*;

public class InputDropdownComponent extends CustomDropdownComponent {
    protected TextBoxComponent textBox;

    public InputDropdownComponent(Sizing horizontalSizing, Sizing verticalSizing, Text title, boolean expanded, int maxLength) {
        super(horizontalSizing, verticalSizing, title, expanded);

        arrowLabel.zIndex(30);

        textBox = Components.textBox(horizontalSizing, "");
        textBox.zIndex(20);
        textBox.positioning(Positioning.relative(0, 0));
        textBox.setMaxLength(maxLength);

        titleDropdown.verticalSizing(Sizing.content());

        updateExpandableDropdown();
    }

    public void onInputChange(OnChanged onChange) {
        textBox.onChanged().subscribe(onChange);
    }

    @Override
    protected void updateExpandableDropdown() {
        if (textBox != null) {
            titleDropdown.horizontalSizing(Sizing.content());

            if (expanded()) {
                titleDropdown.padding(Insets.of(1));
                titleDropdown.verticalSizing(Sizing.fixed(24));
                titleDropdown.child(textBox);
            } else {
                titleDropdown.padding(Insets.of(0));
                titleDropdown.verticalSizing(Sizing.content());
                titleDropdown.removeChild(textBox);
            }
        }

        super.updateExpandableDropdown();
    }
}
