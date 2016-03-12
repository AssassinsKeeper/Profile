package com.zruybal.profile;

/**
 * Created by Zack on 3/11/2016.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class ItemFactory {

    private static Random rand = new Random();

    /**
     * Converts Material -> ItemStack
     */
    public static ItemStack i(Material mat){
        return new ItemStack(mat);
    }

    /**
     * @param maxAmount The maximum amount (exclusive)
     */
    public static ItemStack randAmount(Material mat, int maxAmount){
        return randAmount(mat, 1 ,maxAmount);
    }

    /**
     * @param amount1 The minimum amount (inclusive)
     * @param amount2 The maximum amount (exclusive)
     */
    public static ItemStack randAmount(Material mat, int amount1, int amount2){
        return new ItemStack(mat, rand.nextInt(amount2-amount1)+amount1);
    }

    /**
     * Generates an item from an amount, shorter to type than "new ItemStack(mat, amount)"
     */
    public static ItemStack gen(Material mat, int amount){
        return new ItemStack(mat, amount);
    }

    /**
     * Generates an item and automatically applies amount, name and lore!
     * @param name Set this to null to keep default name!
     */
    public static ItemStack gen(Material mat, int amount, String name, String... lore){
        return modify(i(mat), amount, name, lore);
    }

    public static ItemStack gen(Material mat, int amount, String name, boolean enchanted, String... lore){
        return modify(i(mat), amount, name, enchanted, lore);
    }

    /**
     * Modifies an existing ItemStack to the given parameters!
     * @param name Set this to null to keep default name!
     * @param amount Set this to a -1 to keep existing amount!
     */
    public static ItemStack modify(ItemStack itemStack, int amount, String name, String... lore){
        return modify(itemStack, amount, name, false, lore);
    }

    /**
     * Modifies an existing ItemStack to the given parameters!
     * @param name Set this to null to keep default name!
     * @param amount Set this to a -1 to keep existing amount!
     * @param enchanted Whether this item is enchanted
     */
    public static ItemStack modify(ItemStack itemStack, int amount, String name, boolean enchanted, String... lore){
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore2 = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<String>();
        for(String str:lore)lore2.add(ChatColor.RESET + str);

        if(enchanted && !itemMeta.hasEnchants()){
            itemMeta.addEnchant(Enchantment.LURE, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if(name!=null && !name.equals(""))itemMeta.setDisplayName(ChatColor.RESET + name);
        itemMeta.setLore(lore2);
        if(amount>0)itemStack.setAmount(amount);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}