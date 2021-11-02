package dev.stashy.extrasounds.ui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;

public class SoundMixerGuiDescription extends LightweightGuiDescription
{
    Screen parent;

    public SoundMixerGuiDescription(Screen parent)
    {
        this.parent = parent;
        var fullWidth = 33;

        var root = new WGridPanel(9);
        root.setInsets(Insets.ROOT_PANEL);
        setRootPanel(root);
        root.setSize(254, 220);

        var label = new WLabel(new TranslatableText("options.sounds.title"));
        root.add(label, 0, 0, fullWidth, 2);
        label.setHorizontalAlignment(HorizontalAlignment.CENTER);
        label.setVerticalAlignment(VerticalAlignment.CENTER);
        label.setColor(0xffffff, 0x333333);

        var container = new WGridPanel(9);
        int n = 0;
        int height = 2;
        int width = fullWidth / 2 - 1;
        for (var c : SoundCategory.values())
        {
            var slider = new WLabeledSlider(0, 100, Axis.HORIZONTAL);
            slider.setLabel(new TranslatableText("soundCategory." + c.getName()));
            slider.setValue((int) (MinecraftClient.getInstance().options.getSoundVolume(c) * 100));
            slider.setValueChangeListener((val) -> {
                MinecraftClient.getInstance().options.setSoundVolume(c, val / 100f);
            });
            container.add(slider,
                          1 + ((n % 2 != 0) ? width + 1 : 0), (height + 1) * (n / 2),
                          width, height);
            n++;
        }

        var scroll = new WScrollPanel(container);
        scroll.setScrollingHorizontally(TriState.FALSE);
        root.add(scroll, 0, 2, fullWidth, 19);

        WButton back = new WButton(ScreenTexts.DONE);
        back.setAlignment(HorizontalAlignment.CENTER);
        back.setOnClick(() -> {
            MinecraftClient.getInstance().options.write();
            MinecraftClient.getInstance().setScreen(parent);
        });

        root.add(back, 4, 22, 24, 2);

        root.validate(this);
    }

    @Override
    public void addPainters()
    {

    }
}
