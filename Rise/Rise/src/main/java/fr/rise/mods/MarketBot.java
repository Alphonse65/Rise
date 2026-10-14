package fr.rise.mods;

import fr.rise.*;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Achete bas / revend haut sur le marche du serveur. Menus et regex a adapter a votre serveur. */
public class MarketBot extends Module {
    private final Setting.Text items = add(new Setting.Text("Items (ids)", "diamond,netherite_ingot"));
    private final Setting.Num maxBuy = add(new Setting.Num("Prix achat max", 1000, 1, 100000, true));
    private final Setting.Num markup = add(new Setting.Num("Marge %", 25, 1, 200, true));
    private final Setting.Num budget = add(new Setting.Num("Budget session", 50000, 100, 1000000, true));
    private final Setting.Num scanDelay = add(new Setting.Num("Delai scan (ticks)", 80, 20, 400, true));
    private final Setting.Text openCmd = add(new Setting.Text("Commande marche", "/ah"));
    private final Setting.Text marketTitle = add(new Setting.Text("Titre menu marche", "Auction"));
    private final Setting.Text confirmTitle = add(new Setting.Text("Titre confirmation", "Confirm"));
    private final Setting.Text confirmItem = add(new Setting.Text("Item confirmer (id)", "lime_stained_glass_pane"));
    private final Setting.Text priceRegex = add(new Setting.Text("Regex prix (groupe 1)", "(?i)price[^0-9]*([0-9][0-9,.]*)"));
    private final Setting.Text relistCmd = add(new Setting.Text("Commande vente", "/ah sell {price}"));

    private final Map<String, Double> lastBuy = new HashMap<>();
    private double spent;
    private int cooldown;

    public MarketBot() { super("Market Bot", "Achete bas, revend haut sur le marche du serveur.", Category.BOTS); }

    @Override public void onEnable() { spent = 0; cooldown = 0; lastBuy.clear(); }

    private static String id(ItemStack s) { return Registries.ITEM.getId(s.getItem()).getPath(); }

    @Override public void onTick() {
        if (cooldown > 0) { cooldown--; return; }
        if (spent >= budget.i()) { info("Budget de session atteint."); setEnabled(false); return; }

        Set<String> wanted = new HashSet<>();
        for (String s : items.s().split(",")) wanted.add(s.trim().toLowerCase());

        if (mc.currentScreen instanceof GenericContainerScreen gcs) {
            String title = gcs.getTitle().getString();
            GenericContainerScreenHandler h = gcs.getScreenHandler();
            int size = h.getRows() * 9;

            if (title.contains(confirmTitle.s())) {
                for (int i = 0; i < size; i++)
                    if (id(h.slots.get(i).getStack()).equals(confirmItem.s().trim())) { click(h, h.slots.get(i)); break; }
                cooldown = 10; return;
            }
            if (title.contains(marketTitle.s())) {
                Pattern p;
                try { p = Pattern.compile(priceRegex.s()); } catch (Exception e) { info("Regex invalide."); setEnabled(false); return; }
                for (int i = 0; i < size; i++) {
                    Slot s = h.slots.get(i);
                    ItemStack st = s.getStack();
                    if (st.isEmpty() || !wanted.contains(id(st))) continue;
                    Double price = parsePrice(p, st);
                    if (price == null || price > maxBuy.i() || spent + price > budget.i()) continue;
                    click(h, s);
                    spent += price;
                    lastBuy.put(id(st), price);
                    info(String.format(Locale.US, "Achat %s a %.0f (depense %.0f)", id(st), price, spent));
                    cooldown = 10; return;
                }
                mc.player.closeHandledScreen();   // rien d'interessant : on ferme pour rafraichir
                cooldown = scanDelay.i(); return;
            }
            return;
        }

        for (int i = 0; i < 36; i++) {
            ItemStack st = mc.player.getInventory().getStack(i);
            if (st.isEmpty() || !lastBuy.containsKey(id(st))) continue;
            double sell = lastBuy.remove(id(st)) * (1 + markup.i() / 100.0);
            Bots.toMainHand(i);
            Bots.command(relistCmd.s().replace("{price}", String.format(Locale.US, "%.0f", sell)));
            info(String.format(Locale.US, "Mise en vente %s a %.0f", id(st), sell));
            cooldown = 30; return;
        }
        Bots.command(openCmd.s());
        cooldown = 40;
    }

    private void click(GenericContainerScreenHandler h, Slot s) {
        mc.interactionManager.clickSlot(h.syncId, s.id, 0, SlotActionType.PICKUP, mc.player);
    }

    private Double parsePrice(Pattern p, ItemStack st) {
        StringBuilder sb = new StringBuilder();
        for (Text t : st.getTooltip(Item.TooltipContext.DEFAULT, mc.player, TooltipType.BASIC)) sb.append(t.getString()).append('\n');
        Matcher m = p.matcher(sb.toString());
        if (!m.find()) return null;
        try { return Double.parseDouble(m.group(1).replace(",", "")); } catch (Exception e) { return null; }
    }
}
