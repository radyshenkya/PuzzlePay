package su.puzzle.pay.ui.components;

import net.minecraft.text.Text;

import org.apache.commons.lang3.StringUtils;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.TextBoxComponent.OnChanged;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;

public class InputDropdownComponent extends CustomDropdownComponent {
    protected TextBoxComponent textBox;
    protected int inputFieldLimit;

    public InputDropdownComponent(Sizing horizontalSizing, Sizing verticalSizing, Text title, boolean expanded, int limit) {
        super(horizontalSizing, verticalSizing, title, expanded);

        inputFieldLimit = limit;

        arrowLabel.zIndex(30);

        textBox = Components.textBox(horizontalSizing, "");
        textBox.zIndex(20);
        textBox.positioning(Positioning.relative(0, 0));

        titleDropdown.verticalSizing(Sizing.content());

        updateExpandableDropdown();
    }

    public InputDropdownComponent onInputChange(OnChanged onChange) {
        textBox.onChanged().subscribe(text -> {
            if (text.length() > inputFieldLimit) {
                int cursor = textBox.getCursor();
                textBox.text(StringUtils.truncate(text, inputFieldLimit));
                textBox.setCursor(cursor);
                return;
            }

            onChange.onChanged(text);
        });
        return this;
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
