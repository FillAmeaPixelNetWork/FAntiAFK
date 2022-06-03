package fap.pigeon;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.*;

public class FAntiAFK
        extends PluginBase
        implements Listener
{
    ConcurrentHashMap<String, Location> loc = new ConcurrentHashMap();
    ConcurrentHashMap<String, Long> Time = new ConcurrentHashMap();

    public void onEnable()
    {
        getServer().getLogger().info(TextFormat.GREEN + "[FAntiAFK]防挂机插件已启动！版本：1.0.1");
        getServer().getLogger().info(TextFormat.YELLOW + "[FAntiAFK]作者：从不咕咕的鸽纸。如有问题，请在原贴留言或前往Github提交issues。");
        getServer().getLogger().info(TextFormat.GOLD + "[FAntiAFK]本插件Github开源地址：https://github.com/FillAmeaPixelNetWork/FAntiAFK");
        File file = new File(getDataFolder() + "/config.yml");
        if (!file.exists())
        {
            Config config = new Config(file.getPath(), 2);
            config.set("挂机检测范围", 3.0D);
            config.set("挂机检测时长", 60);
            config.set("挂机踢出提示语", String.valueOf("§c检测到您长时间未进行操作，请重新登录游戏"));
            config.save();
        }
        final Config config = new Config();
        config.load(file.getPath());
        getServer().getScheduler().scheduleRepeatingTask(new Task()
        {
            public void onRun(int i)
            {
                for (Player player : FAntiAFK.this.getServer().getOnlinePlayers().values())
                {
                    if (!FAntiAFK.this.loc.containsKey(player.getName())) {
                        FAntiAFK.this.loc.put(player.getName(), player.getLocation());
                    }
                    if (!FAntiAFK.this.Time.containsKey(player.getName())) {
                        FAntiAFK.this.Time.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
                    }
                    if (((Location) FAntiAFK.this.loc.get(player.getName())).getLevel() != player.getLevel())
                    {
                        FAntiAFK.this.Time.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
                        FAntiAFK.this.loc.put(player.getName(), player.getLocation());
                    }
                    if (((Location) FAntiAFK.this.loc.get(player.getName())).distance(player.getLocation()) > config.getDouble("挂机检测范围"))
                    {
                        FAntiAFK.this.Time.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
                        FAntiAFK.this.loc.put(player.getName(), player.getLocation());
                    }
                    if (System.currentTimeMillis() / 1000L % 60L - ((Long) FAntiAFK.this.Time.get(player.getName())).longValue() / 1000L % 60L >= config.getInt("挂机检测时长"))
                    {
                        player.kick(config.getString("挂机踢出提示语"));
                        FAntiAFK.this.Time.remove(player.getName());
                        FAntiAFK.this.loc.remove(player.getName());
                    }
                }
            }
        }, 20);
    }
}
