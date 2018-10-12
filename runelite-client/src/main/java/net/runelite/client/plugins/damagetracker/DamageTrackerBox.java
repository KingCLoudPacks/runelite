package net.runelite.client.plugins.damagetracker;

import com.google.common.base.Strings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;


@Getter
public class DamageTrackerBox extends JPanel {

    @Getter
    @Setter
    private DamageTrackerItemEntry entry;
    private JLabel damageLabel;

    public DamageTrackerBox(DamageTrackerItemEntry entry, final String title, final String subTitle) {
        this.entry = entry;
        damageLabel = new JLabel();

        setLayout(new BorderLayout(0, 1));
        setBorder(new EmptyBorder(5, 0, 0, 0));

        final JPanel logTitle = new JPanel(new BorderLayout(5, 0));
        logTitle.setBorder(new EmptyBorder(7, 7, 7, 7));
        logTitle.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());

        final JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontManager.getRunescapeSmallFont());
        titleLabel.setForeground(Color.WHITE);

        logTitle.add(titleLabel, BorderLayout.WEST);

        if (!Strings.isNullOrEmpty(subTitle)) {
            final JLabel subTitleLabel = new JLabel(subTitle);
            subTitleLabel.setFont(FontManager.getRunescapeSmallFont());
            subTitleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            logTitle.add(subTitleLabel, BorderLayout.CENTER);
        }

        final JPanel itemContainer = new JPanel(new GridLayout(1, 1, 1, 1));

        damageLabel.setFont(FontManager.getRunescapeSmallFont());
        damageLabel.setForeground(Color.WHITE);

        final JPanel damageContainer = new JPanel();
        damageContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        damageContainer.add(damageLabel);

        itemContainer.add(damageContainer);

        add(logTitle, BorderLayout.NORTH);
        add(itemContainer, BorderLayout.CENTER);

        updateEntry();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DamageTrackerBox that = (DamageTrackerBox) o;
        return Objects.equals(entry, that.entry);
    }

    @Override
    public int hashCode() {

        return Objects.hash(entry);
    }

    public void updateEntry() {
        damageLabel.setText(String.format("Damage Inflicted: %d", entry.getDamageDone()));
    }


}
