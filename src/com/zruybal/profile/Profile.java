package com.zruybal.profile;

/**
 * Created by Zack on 3/11/2016.
 */

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import com.zruybal.profile.IconMenu.OptionClickEvent;
import com.zruybal.profile.IconMenu.WindowCloseEvent;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Profile extends JavaPlugin implements Listener {

    private static Profile instance;
    private Economy vaultEco;

    public static Profile getInstance(){
        return instance;
    }

    public void onDisable(){
        saveConfig();
    }

    public void onEnable(){
        instance=this;
        Bukkit.getPluginManager().registerEvents(this, this);

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            vaultEco = economyProvider.getProvider();
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED+"That command can only be used by an ingame player!");
                return true;
            }

            if(label.equals("profile")){
                if(args.length==1){
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target!=null){
                        IconMenu inv = new IconMenu(target.getName()+"'s Profile", 36, new IconMenu.OptionClickEventHandler() {
                            public boolean onOptionClick(OptionClickEvent event) { return false; }
                        }, new IconMenu.WindowCloseEventHandler() {
                            public boolean onWindowClose(WindowCloseEvent event) { return true; }
                        });

                        Resident res = TownyUniverse.getDataSource().getResident(target.getName());

                        if(res.hasTown()){
                            inv.setOption(0, ItemFactory.gen(Material.NETHER_STAR, 1, ChatColor.GOLD+"Town", true, ChatColor.DARK_PURPLE+res.getTown().getName()));

                            ItemStack mayor = ItemFactory.gen(Material.SKULL_ITEM, 1, ChatColor.GOLD+"Town Mayor", ChatColor.DARK_PURPLE+res.getTown().getMayor().getName());
                            mayor.setDurability((short)3);
                            SkullMeta meta = ((SkullMeta)mayor.getItemMeta());
                            meta.setOwner(res.getTown().getMayor().getName());
                            mayor.setItemMeta(meta);
                            inv.setOption(18, mayor);

                            inv.setOption(27, ItemFactory.gen(Material.CHEST, 1, ChatColor.GOLD+"Town Balance", true, ChatColor.DARK_PURPLE+res.getTown().getHoldingFormattedBalance()));
                            if(res.getTown().hasNation()){
                                inv.setOption(9, ItemFactory.gen(Material.NETHER_STAR, 1, ChatColor.GOLD+"Nation", true, ChatColor.DARK_PURPLE+res.getTown().getNation().getName()));
                            } else {
                                inv.setOption(9, ItemFactory.gen(Material.BARRIER, 1, ChatColor.GOLD+"No Nation"));
                            }
                        } else {
                            inv.setOption(0, ItemFactory.gen(Material.BARRIER, 1, ChatColor.GOLD+"No Town"));
                            inv.setOption(9, ItemFactory.gen(Material.BARRIER, 1, ChatColor.GOLD+"No Nation"));
                            inv.setOption(18, ItemFactory.gen(Material.BARRIER, 1, ChatColor.GOLD+"No Town"));
                            inv.setOption(27, ItemFactory.gen(Material.BARRIER, 1, ChatColor.GOLD+"No Town"));
                        }

                        String biotext = getConfig().getString(target.getUniqueId().toString());
                        if(biotext!=null){
                            int substringIndex = 40;
                            for(int i=0; i<20; i++){
                                if(substringIndex>=biotext.length()){
                                    substringIndex=biotext.length();
                                    break;
                                }
                                if(biotext.charAt(substringIndex-1)==' ')break;
                                substringIndex++;
                            }
                            ItemStack bio = ItemFactory.gen(Material.BOOK, 1, ChatColor.GOLD+target.getName()+"'s Autobiography", true,
                                    ChatColor.DARK_PURPLE+biotext.substring(0, substringIndex),
                                    ChatColor.DARK_PURPLE+biotext.substring(substringIndex));
                            inv.setOption(12, bio);
                        } else {
                            inv.setOption(12, ItemFactory.gen(Material.BOOK, 1, ChatColor.GOLD+"No Autobiography"));
                        }

                        ItemStack noArmor = ItemFactory.gen(Material.BARRIER, 1, ChatColor.GOLD+"No Armor");
                        ItemStack noItem = ItemFactory.gen(Material.BARRIER, 1, ChatColor.GOLD+"No Item");
                        inv.setOption(4, target.getInventory().getHelmet()==null?noArmor:target.getInventory().getHelmet());
                        inv.setOption(13, target.getInventory().getChestplate()==null?noArmor:target.getInventory().getChestplate());
                        inv.setOption(14, target.getInventory().getItemInMainHand().getType().equals(Material.AIR)?noItem:target.getInventory().getItemInMainHand());
                        inv.setOption(15, target.getInventory().getItemInOffHand().getType().equals(Material.AIR)?noItem:target.getInventory().getItemInOffHand());
                        inv.setOption(22, target.getInventory().getLeggings()==null?noArmor:target.getInventory().getLeggings());
                        inv.setOption(31,target.getInventory().getBoots()==null?noArmor: target.getInventory().getBoots());

                        inv.setOption(8, ItemFactory.gen(Material.ITEM_FRAME, 1, ChatColor.GOLD+"Rank", true, ChatColor.DARK_PURPLE+PermissionsEx.getUser(target).getParents().get(0).getPrefix()));
                        inv.setOption(17, ItemFactory.gen(Material.CHEST, 1, ChatColor.GOLD+"Balance", true, ChatColor.DARK_PURPLE+"$"+vaultEco.getBalance(Bukkit.getOfflinePlayer(target.getUniqueId()))));
                        inv.setOption(26, ItemFactory.gen(Material.DIAMOND_SWORD, 1, ChatColor.GOLD+"McMMO Swords", true, ChatColor.DARK_PURPLE+""+ExperienceAPI.getLevel(target, SkillType.SWORDS.getName())+" Levels"));
                        inv.setOption(35, ItemFactory.gen(Material.DIAMOND_AXE, 1, ChatColor.GOLD+"McMMO Axes", true, ChatColor.DARK_PURPLE+""+ExperienceAPI.getLevel(target, SkillType.AXES.getName())+" Levels"));


                        inv.open((Player)sender);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED+"Cannot find player: " + args[0]);
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Not enough or too little arguments. Usage: /profile <player>");
                    return true;
                }
            } else if(label.equals("setbio")){
                if(args.length>=1){
                    getConfig().set(((Player)sender).getUniqueId().toString(), StringUtils.join(args, " "));
                    sender.sendMessage(ChatColor.GREEN+"Your bio has been set!");
                } else {
                    sender.sendMessage(ChatColor.RED+"Did you type your bio? Usage: /setbio <bio>");
                    return true;
                }
            }
        } catch(Exception e){
            sender.sendMessage(ChatColor.RED+"Oh crap, something went wrong. Please report on our website.");
        }
        return true;

    }
}