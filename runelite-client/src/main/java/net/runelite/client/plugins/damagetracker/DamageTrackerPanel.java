package net.runelite.client.plugins.damagetracker;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Actor;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ColorUtil;

public class DamageTrackerPanel extends PluginPanel {
    private static final String HTML_LABEL_TEMPLATE =
            "<html><body style='color:%s'>%s<span style='color:white'>%s</span></body></html>";

    // When there is no loot, display this
    private final PluginErrorPanel errorPanel = new PluginErrorPanel();

    // Handle loot logs
    private final JPanel logsContainer = new JPanel();

    // Handle overall session data
    private final JPanel overallPanel = new JPanel();
    private final JLabel overallDamageLabel = new JLabel();
    private final JLabel totalTargetsLabel = new JLabel();
    private final JLabel overallIcon = new JLabel();
    private final DamageTrackerPlugin plugin;
    private int overallCount;
    private int overallDamage;

    private final HashSet<DamageTrackerBox> targetBoxEntries = new HashSet<>();

    DamageTrackerPanel(final DamageTrackerPlugin plugin) {
        this.plugin = plugin;
        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        final JPanel layoutPanel = new JPanel();
        layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
        add(layoutPanel, BorderLayout.NORTH);

        overallPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        overallPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        overallPanel.setLayout(new BorderLayout());
        overallPanel.setVisible(false);

        final JPanel overallInfo = new JPanel();
        overallInfo.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        overallInfo.setLayout(new GridLayout(2, 1));
        overallInfo.setBorder(new EmptyBorder(0, 10, 0, 0));
        overallDamageLabel.setFont(FontManager.getRunescapeSmallFont());
        totalTargetsLabel.setFont(FontManager.getRunescapeSmallFont());
        overallInfo.add(overallDamageLabel);
        overallInfo.add(totalTargetsLabel);
        overallPanel.add(overallIcon, BorderLayout.WEST);
        overallPanel.add(overallInfo, BorderLayout.CENTER);

        final JMenuItem reset = new JMenuItem("Remove All");
        reset.addActionListener(e ->
        {
            overallCount = 0;
            overallDamage = 0;
            targetBoxEntries.clear();
            updateOverall();
            logsContainer.removeAll();
            logsContainer.repaint();
        });

        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(reset);
        overallPanel.setComponentPopupMenu(popupMenu);

        logsContainer.setLayout(new BoxLayout(logsContainer, BoxLayout.Y_AXIS));
        layoutPanel.add(overallPanel);
        layoutPanel.add(logsContainer);

        errorPanel.setContent("Damage Trackers", "You have not done any damage yet.");
        add(errorPanel);
    }

    void loadHeaderIcon(BufferedImage img) {
        overallIcon.setIcon(new ImageIcon(img));
    }

    private static String htmlLabel(String key, Object value) {
        return String.format(HTML_LABEL_TEMPLATE, ColorUtil.toHexColor(ColorScheme.LIGHT_GRAY_COLOR), key, value);
    }

    public void addLog(final DamageTrackerItemEntry entry) {

        remove(errorPanel);
        overallPanel.setVisible(true);

        final String subTitle = entry.getLevel() > -1 ? "(lvl-" + entry.getLevel() + ")" : "";
        final DamageTrackerBox box = new DamageTrackerBox(entry, entry.getName(), subTitle);
        targetBoxEntries.add(box);
        logsContainer.add(box, 0);
        logsContainer.repaint();

        overallCount += 1;
        overallDamage += entry.getDamageDone();
        updateOverall();

        final JMenuItem reset = new JMenuItem("Remove");
        reset.addActionListener(e ->
        {
            overallCount -= 1;
            overallDamage -= entry.getDamageDone();
            targetBoxEntries.removeIf(b -> b.getEntry().equals(entry));
            updateOverall();
            logsContainer.remove(box);
            logsContainer.repaint();
        });

        // Create popup menu
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        popupMenu.add(reset);
        box.setComponentPopupMenu(popupMenu);
    }

    public void updateEntries(HashMap<Actor, DamageTrackerItemEntry> targetMap) {
        for (Actor actor : targetMap.keySet()) {
            Optional<DamageTrackerBox> entryOptional = targetBoxEntries.stream().filter(b -> b.getEntry().getName().equals(actor.getName())).findFirst();
            if (!entryOptional.isPresent()) {
                SwingUtilities.invokeLater(() -> addLog(targetMap.get(actor)));
                continue;
            }
        }
    }

    public DamageTrackerBox getEntryBox(Actor actor) {
        Optional<DamageTrackerBox> entryOptional = targetBoxEntries.stream().filter(b -> b.getEntry().getName().equals(actor.getName())).findFirst();
        if (entryOptional.isPresent())
            return entryOptional.get();
        return null;
    }

    public void addTotalDamage(int dmg) {
        overallDamage += dmg;
        updateOverall();
    }

    private void updateOverall() {
        overallDamageLabel.setText(htmlLabel("Total Damage: ", overallDamage));
        totalTargetsLabel.setText(htmlLabel("Targets: ", overallCount));
    }
}