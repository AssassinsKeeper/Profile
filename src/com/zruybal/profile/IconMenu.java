package com.zruybal.profile;

/**
 * Created by Zack on 3/11/2016.
 */
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class IconMenu implements Listener {
    private String name;
    private int size;
    private OptionClickEventHandler handler;
    private WindowCloseEventHandler handler2;
    private Plugin plugin = Profile.getInstance();
    private boolean canMoveItems=false;

    private ItemStack[] optionIcons;

    public IconMenu(String name, int size, OptionClickEventHandler handler, WindowCloseEventHandler handler2) {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.handler2 = handler2;
        this.optionIcons = new ItemStack[size];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public IconMenu setMoveItems(boolean bool){
        canMoveItems=bool;
        return this;
    }

    public IconMenu setOption(int position, ItemStack item) {
        optionIcons[position] = item;
        return this;
    }

    public ItemStack getOption(int position){
        return optionIcons[position];
    }

    public IconMenu fillEmpty(ItemStack item) {
        for(int i=0; i<size; i++){
            if(optionIcons[i]==null){
                optionIcons[i]=item;
            }
        }
        return this;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        handler = null;
        handler2 = null;
        plugin = null;
        optionIcons = null;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().getTitle().equals(name)) {
            int slot = event.getRawSlot();

            if(!canMoveItems)event.setCancelled(true);

            if (slot >= 0 && slot < size) {
                if(!(event.getAction().equals(InventoryAction.PICKUP_ALL)
                        || event.getAction().equals(InventoryAction.PICKUP_HALF)
                        || event.getAction().equals(InventoryAction.PLACE_ALL)
                        || event.getAction().equals(InventoryAction.PLACE_ONE)
                        || event.getAction().equals(InventoryAction.DROP_ALL_SLOT)
                        || event.getAction().equals(InventoryAction.DROP_ONE_SLOT)
                        || event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR))){

                    event.setCancelled(true);
                    return;
                }
                Plugin plugin = this.plugin;
                OptionClickEvent e = new OptionClickEvent((Player)event.getWhoClicked(), event.getClickedInventory(), slot);
                boolean bool = handler.onOptionClick(e)==false;
                if(e.getCancelled())event.setCancelled(true);
                if(bool)return;

                final Player p = (Player)event.getWhoClicked();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        p.closeInventory();
                    }
                }, 1);

                destroy();
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClose(InventoryCloseEvent event){
        if(event.getInventory().getTitle().equals(name)) {
            WindowCloseEvent e = new WindowCloseEvent((Player)event.getPlayer(), event.getInventory());
            if(handler2.onWindowClose(e)==false){
                open((Player)event.getPlayer());
            } else {
                destroy();
            }
        }
    }

    public interface OptionClickEventHandler {
        public boolean onOptionClick(OptionClickEvent event);
    }

    public class OptionClickEvent {
        private Player player;
        private int position;
        private Inventory inv;
        private boolean cancelledMovement;

        public OptionClickEvent(Player player, Inventory inv, int position) {
            this.player = player;
            this.inv = inv;
            this.position = position;
        }

        public void setCancelled(boolean bool){
            cancelledMovement=bool;
        }

        public boolean getCancelled(){
            return cancelledMovement;
        }

        public Player getPlayer() {
            return player;
        }

        public Inventory getInventory(){
            return inv;
        }

        public int getPosition() {
            return position;
        }
    }

    public interface WindowCloseEventHandler {
        public boolean onWindowClose(WindowCloseEvent event);
    }

    public class WindowCloseEvent {
        private Player player;
        private Inventory inv;

        public WindowCloseEvent(Player player, Inventory inv) {
            this.player = player;
            this.inv = inv;
        }

        public Player getPlayer() {
            return player;
        }

        public Inventory getInventory(){
            return inv;
        }
    }
}
