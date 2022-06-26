package org.hftm.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.hftm.MonitoringFX;

import javafx.scene.image.Image;

public class ImageResources {

    HashMap<String, String> images = new HashMap<String, String>();

    //public static final String DIRECTORY_PATH = Paths.get("..", "view", "res").toString();  

    public Image get(String key) {
        String path = MonitoringFX.class.getResource(Paths.get("view", "res", images.get(key)).toString()).toString();
        return new Image(path);
    }

    public ImageResources() {
        images.put("START_LOGO", "014-double-chevron.png");
        images.put("PAUSE_LOGO", "009-stop.png");
    }
}
