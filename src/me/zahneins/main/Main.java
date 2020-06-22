package me.zahneins.main;

import org.bukkit.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    public HashMap<Player,LocalTime> Jailtime = new HashMap<Player,LocalTime>();
    public List<Player> playerList = new ArrayList<Player>();

     @Override
    public void onEnable(){
         getConfig().options().copyDefaults(true);
         saveConfig();
         Bukkit.getServer().getPluginManager().registerEvents(this,this);
         Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "Plugin" + ChatColor.GRAY + " : " + ChatColor.GREEN + "Enable");
         new CheckJail().runTaskTimer(this, 0, 20);

     }

     @Override
    public void onDisable(){

         Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "Plugin" + ChatColor.GRAY + " : " + ChatColor.RED + "Disable");

     }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Player player = (Player) sender;
        if(!(sender instanceof Player)){
            return true;
        }
        if(command.getName().equalsIgnoreCase("stj")){
            if(sender.isOp()) {
                    if(args.length <= 0){
                        player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj setjail - เพื่อบันทึกโลเคชั่นของคุก");
                        player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj setoutjail - เพื่อบันทึกโลเคชั่นของคุก");
                        player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj give ชื่อผู้เล่น - เพื่อสร้างไอเทม");
                        return true;
                    }
                    if(args.length == 1) {
                        if (args[0].equalsIgnoreCase("setjail")) {
                            this.getConfig().set("Locationjail",player.getLocation());
                            saveConfig();
                            player.sendMessage(ChatColor.GREEN  + "บันทึกโลเคชั่นของคุก");
                            return true;
                        }
                        else if (args[0].equalsIgnoreCase("setoutjail")) {
                            this.getConfig().set("Locationoutjail",player.getLocation());
                            saveConfig();
                            player.sendMessage(ChatColor.GREEN  + "บันทึกโลเคชั่นของจุดออกคุก");
                            return true;
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj setjail - เพื่อบันทึกโลเคชั่นของคุก");
                            player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj setoutjail - เพื่อบันทึกโลเคชั่นของคุก");
                            player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj give ชื่อผู้เล่น - เพื่อสร้างไอเทม");
                            return true;
                        }
                    }

                    if(args.length >= 2){
                        if(args[0].equalsIgnoreCase("give")){

                            ItemStack itemStack = new ItemStack(Material.STICK);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            List<String> metalist = new ArrayList<String>();
                            itemMeta.setDisplayName(ChatColor.GOLD + "Send To Jail");
                            metalist.add(ChatColor.GRAY + "[ไอเทมสำหรับตำรวจเท่านั้น]");
                            metalist.add(ChatColor.GREEN + "ใช้ในการส่งผู้เล่นเข้าคุก");
                            metalist.add("");
                            metalist.add(ChatColor.GREEN + "ครอบครองโดย : " + ChatColor.YELLOW + args[1]);
                            itemMeta.setLore(metalist);
                            itemStack.setItemMeta(itemMeta);
                            player.getInventory().addItem(itemStack);
                            player.sendMessage( ChatColor.GREEN + "สร้างไอเทมสำหรับตำรวจชื่อ" + ChatColor.GRAY + " : " + ChatColor.GREEN + args[1] );
                            return true;
                        }
                        else {
                            player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj setjail - เพื่อบันทึกโลเคชั่นของคุก");
                            player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj setoutjail - เพื่อบันทึกโลเคชั่นของคุก");
                            player.sendMessage(ChatColor.RED + "ใช้คำสั่งโดย /stj give ชื่อผู้เล่น - เพื่อสร้างไอเทม");
                            return true;
                        }
                    }


                return true;
            }
            else {
                player.sendMessage(ChatColor.RED + "คุณไม่มีสิทธิ์ใช้คำสั่งนี้");
            }

            return true;
        }

        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event){
         Player player = event.getPlayer();
         if(event.getRightClicked().getType().equals(EntityType.PLAYER)){
             if(player.getItemInHand().getType() == Material.STICK){
                 if(player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Send To Jail")) {
                     String Check = player.getItemInHand().getItemMeta().getLore().get(3);
                     if(Check.contains("ครอบครองโดย")){
                         if(Check.contains(player.getName()) || Check.contains(player.getDisplayName())){
                             Player tpPlayer = Bukkit.getServer().getPlayer(event.getRightClicked().getName());
                             if(!(playerList.contains(tpPlayer))) {
                                 Location location = (Location) this.getConfig().get("Locationjail");
                                 tpPlayer.teleport(location);
                                 player.sendMessage(ChatColor.RED + "[แจ้งเหตุ]" + ChatColor.GRAY + " : " + ChatColor.GREEN + "ท่านได้ส่ง " + event.getRightClicked().getName() + " เข้าคุกแล้ว");
                                 tpPlayer.sendMessage(ChatColor.RED + "[แจ้งเหตุ]" + ChatColor.GRAY + " : " + ChatColor.GREEN + "ท่านได้ถูก " + player.getName() + " ส่งมายังคุก");
                                 player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                                 tpPlayer.playSound(tpPlayer.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                                 tpPlayer.sendMessage(ChatColor.RED + "คุณถูกจำคุกเป็นเวลา : 5 นาที");
                                 tpPlayer.sendMessage(ChatColor.AQUA + "เวลาที่จะได้รับอิสระภาพ : " + LocalTime.now().plusMinutes(5));

                                 /* ลบไอเทมผู้เล่นทั้งหมดเมื่อโดนส่งเข้าคุก
                                 tpPlayer.getInventory().clear();
                                 tpPlayer.getInventory().setHelmet(null);
                                 tpPlayer.getInventory().setChestplate(null);
                                 tpPlayer.getInventory().setLeggings(null);
                                 tpPlayer.getInventory().setBoots(null);
                                 tpPlayer.updateInventory();
                                 */
                                 playerList.add(tpPlayer);
                                 Jailtime.put(tpPlayer, LocalTime.now().plusMinutes(5));
                             }
                             else {
                                 player.sendMessage(ChatColor.RED + "ผู้เล่นนี้อยู่ในระหว่างการจำคุก");
                             }
                         }
                         else {
                             return;
                         }
                     }
                     else {
                         return;
                     }
                 }
                 return;
             }
            else if (player.getItemInHand() == null) {
                 return;
             }
            else if(player.getItemInHand().getType() != Material.COMPASS) {
                 return;
             }
            else {return;}
         }
         else {
             return;
         }

    }
    //ห้ามพิมพ์ /spawn เมื่ออยู่ในคุก
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
         Player CommandPlayer = event.getPlayer();
        if(!event.getMessage().equals("spawn")){
            if(playerList.contains(CommandPlayer)) {
                event.getPlayer().sendMessage(ChatColor.RED + "ไม่สามารถใช้คำสั่งได้เมื่ออยู่ในคุก");
                event.setCancelled(true);
            }
        }
    }

    public class CheckJail extends BukkitRunnable {
        @Override
        public void run() {
            if(playerList.size() >= 1) {
                try {
                    for (Player player : playerList) {
                        if (Jailtime.get(player) != null) {
                            LocalTime localTime = LocalTime.now();
                            if (Jailtime.get(player).compareTo(localTime) < 0) {
                                Player tpPlayer = Bukkit.getServer().getPlayer(player.getName());
                                if (player.isOnline()) {
                                    Location location = (Location) getConfig().get("Locationoutjail");
                                    tpPlayer.teleport(location);
                                    tpPlayer.sendMessage(ChatColor.AQUA + "คุณได้รับอิสระภาพ");
                                    Jailtime.remove(player);
                                    playerList.remove(player);
                                } else if (!(player.isOnline())) {
                                    Jailtime.remove(player);
                                    playerList.remove(player);
                                    return;
                                }

                            }
                        } else {
                            return;
                        }
                    }
                }catch (Exception e){

                }

            }

            }
        }
    }



