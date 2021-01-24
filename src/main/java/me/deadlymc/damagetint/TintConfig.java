package me.deadlymc.damagetint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.io.*;

public class TintConfig
{
    private static TintConfig INSTANCE;
    private final File config = new File(getConfigDirectory(), "damage_tint.json");
    
    private int health;
    
    public void init()
    {
        this.health = 20;
        JsonObject object = new JsonObject();
        object.addProperty("health", this.health);
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFile()));
            writer.write(object.toString());
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void update(Integer health)
    {
        JsonParser parser = new JsonParser();
        try
        {
            Object obj = parser.parse(new FileReader(getFile()));
            JsonObject json = (JsonObject) obj;
            if (health != null)
            {
                json.addProperty("health", health);
                FileWriter writer = new FileWriter(getFile());
                writer.write(json.toString());
                writer.close();
            }
            this.setHealth(json.get("health").getAsInt());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public int getHealth()
    {
        return health;
    }
    
    public void setHealth(int health)
    {
        this.health = health;
    }
    
    public File getFile()
    {
        return config;
    }
    
    public File getConfigDirectory()
    {
        File configDir = new File(MinecraftClient.getInstance().runDirectory, "config");
        //noinspection ResultOfMethodCallIgnored
        configDir.mkdir();
        return configDir;
    }
    
    public static TintConfig instance()
    {
        return INSTANCE == null ? (INSTANCE = new TintConfig()) : INSTANCE;
    }
}
