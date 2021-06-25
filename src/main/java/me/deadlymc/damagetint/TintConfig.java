package me.deadlymc.damagetint;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class TintConfig
{
    private static TintConfig INSTANCE;
    private final File config = new File(getConfigDirectory(), "damage_tint.json");
    
    private Float health;
    private Boolean dynamic;
    
    public void init()
    {
        this.health = 20F;
        this.dynamic = true;
        JsonObject object = new JsonObject();
        object.addProperty("health", this.health);
        object.addProperty("dynamic", this.dynamic);
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
    
    public void update(Float health)
    {
        try
        {
            File jsonFile = getFile();
            String jsonString = FileUtils.readFileToString(jsonFile, Charsets.UTF_8);
            JsonElement jelement = new JsonParser().parse(jsonString);
            JsonObject jobject = jelement.getAsJsonObject();
            if (health != null)
            {
                jobject.addProperty("health", health);
                // Write the json to the file
                String resultingJson = new Gson().toJson(jelement);
                FileUtils.writeStringToFile(jsonFile, resultingJson, Charsets.UTF_8);
            }
            this.health = jobject.get("health").getAsFloat();
            this.dynamic = jobject.get("dynamic").getAsBoolean();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void dynamic()
    {
        try
        {
            File jsonFile = getFile();
            String jsonString = FileUtils.readFileToString(jsonFile, Charsets.UTF_8);
            JsonElement jelement = new JsonParser().parse(jsonString);
            JsonObject jobject = jelement.getAsJsonObject();
            jobject.addProperty("dynamic", !this.dynamic);
            // Write the json to the file
            String resultingJson = new Gson().toJson(jelement);
            FileUtils.writeStringToFile(jsonFile, resultingJson, Charsets.UTF_8);

            this.dynamic = jobject.get("dynamic").getAsBoolean();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public Float getHealth()
    {
        return health;
    }

    public Boolean isDynamic()
    {
        return this.dynamic;
    }

    public boolean needsUpdate()
    {
        return this.dynamic == null || this.health == null;
    }
    
    public File getFile()
    {
        return config;
    }
    
    public File getConfigDirectory()
    {
        File configDir = new File(Minecraft.getInstance().gameDir, "config");
        //noinspection ResultOfMethodCallIgnored
        configDir.mkdir();
        return configDir;
    }
    
    public static TintConfig instance()
    {
        return INSTANCE == null ? (INSTANCE = new TintConfig()) : INSTANCE;
    }
}
