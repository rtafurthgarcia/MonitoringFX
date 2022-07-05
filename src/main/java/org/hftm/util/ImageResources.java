package org.hftm.util;

import java.nio.file.Paths;
import java.util.HashMap;

import org.hftm.MonitoringFX;

import javafx.scene.image.Image;

public class ImageResources {

    HashMap<String, String> images = new HashMap<String, String>();

    public Image get(String key) {
        String path = MonitoringFX.class.getResource("view/res/"+ images.get(key)).toString();
        return new Image(path);
    }

    public ImageResources() {
        images.put("START_LOGO", "014-double-chevron.png");
        images.put("PAUSE_LOGO", "009-stop.png");
        images.put("ALL_OK", "011-cloud-computing-ok.png");
        images.put("ALL_NOK", "012-cloud-computing-nok.png");
    }
}
