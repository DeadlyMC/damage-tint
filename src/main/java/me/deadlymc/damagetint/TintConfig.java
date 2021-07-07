package me.deadlymc.damagetint;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class TintConfig
{
    private static TintConfig INSTANCE;
    private final File config = new File(getConfigDirectory(), "damage_tint.json");
    
    private float health;
    private boolean dynamic;
    
    public void init()
    {
        this.health = 20F;
        this.dynamic = true;
        JsonObject object = new JsonObject();
        object.addProperty("health", this.health);
        object.addProperty("dynamic", this.dynamic);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFile()));
            writer.write(object.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update()
    {
        try {
            File jsonFile = getFile();
            String jsonString = FileUtils.readFileToString(jsonFile, Charsets.UTF_8);
            JsonElement jelement = new JsonParser().parse(jsonString);
            JsonObject jobject = jelement.getAsJsonObject();
            if (jobject.get("health") == null) {
                jobject.addProperty("health", 20F);
                this.health = 20F; // Update variable
            }
            if (jobject.get("dynamic") == null) {
                jobject.addProperty("dynamic", true);
                this.dynamic = true; // Update variable
            }
            this.health = jobject.get("health").getAsFloat();
            this.dynamic = jobject.get("dynamic").getAsBoolean();

            // Write the json to the file
            String resultingJson = new Gson().toJson(jelement);
            FileUtils.writeStringToFile(jsonFile, resultingJson, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void update(float health, boolean dynamic)
    {
        try {
            File jsonFile = getFile();
            String jsonString = FileUtils.readFileToString(jsonFile, Charsets.UTF_8);
            JsonElement jelement = new JsonParser().parse(jsonString);
            JsonObject jobject = jelement.getAsJsonObject();
            jobject.addProperty("health", health);
            jobject.addProperty("dynamic", dynamic);
            // Write the json to the file
            String resultingJson = new Gson().toJson(jelement);
            FileUtils.writeStringToFile(jsonFile, resultingJson, Charsets.UTF_8);
            // Update variables
            this.health = health;
            this.dynamic = dynamic;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dynamic(boolean dynamic)
    {
        try {
            File jsonFile = getFile();
            String jsonString = FileUtils.readFileToString(jsonFile, Charsets.UTF_8);
            JsonElement jelement = new JsonParser().parse(jsonString);
            JsonObject jobject = jelement.getAsJsonObject();
            jobject.addProperty("dynamic", dynamic);
            // Write the json to the file
            String resultingJson = new Gson().toJson(jelement);
            FileUtils.writeStringToFile(jsonFile, resultingJson, Charsets.UTF_8);
            this.dynamic = jobject.get("dynamic").getAsBoolean(); // Update variables
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public float getHealth()
    {
        return health;
    }

    public boolean isDynamic()
    {
        return this.dynamic;
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
