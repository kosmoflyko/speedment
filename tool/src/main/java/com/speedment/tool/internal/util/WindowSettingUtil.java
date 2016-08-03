package com.speedment.tool.internal.util;

import com.speedment.common.logger.Logger;
import com.speedment.common.logger.LoggerManager;
import static com.speedment.runtime.util.StaticClassUtil.instanceNotAllowed;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.stage.Stage;

/**
 * Utility class for method related to window settings, such as size, position 
 * and whether the window was maximized or not.
 *
 * @author Simon Jonasson
 * @since 3.0.0
 */
public class WindowSettingUtil {    
    private final static Logger LOGGER = LoggerManager.getLogger(WindowSettingUtil.class);
    private final static Preferences PREFERENCES = Preferences.userNodeForPackage(WindowSettingUtil.class);
    
    private final static String WINDOW_WIDTH = "windowWidth";
    private final static String WINDOW_HEIGHT = "windowHeight";
    private final static String WINDOW_X_POS = "windowXPos";
    private final static String WINDOW_Y_POS = "windowYPos";
    private final static String WINDOW_MAXIMIZED = "windowMaximized";
    
    
    private final static double DEFUALT_WIDTH  = 1280;
    private final static double DEFUALT_HEIHGT = 720;
    private final static double DEFUALT_X = 0;
    private final static double DEFUALT_Y = 0;
    
    /**
     * Retrieves data about he window settings from the previous session and applies 
     * them to the stage. These settings include window size, position and if
     * it was maximized or not.
     * 
     * @param stage  the stage to apply these settings to
     * @param name   the name under which we stored the settings
     */
    public static void applyStoredDisplaySettings(Stage stage, String name){
        try {
            if( PREFERENCES.nodeExists(name) ){
                Preferences stagePreferences = PREFERENCES.node(name);
                boolean wasMaximized = stagePreferences.getBoolean(WINDOW_MAXIMIZED, false);
                if( wasMaximized ){
                    stage.setMaximized(true);
                } else {
                    stage.setX( stagePreferences.getDouble(WINDOW_X_POS, DEFUALT_X));
                    stage.setY( stagePreferences.getDouble(WINDOW_Y_POS, DEFUALT_Y));
                    stage.setWidth( stagePreferences.getDouble(WINDOW_WIDTH,  DEFUALT_WIDTH));
                    stage.setHeight(stagePreferences.getDouble(WINDOW_HEIGHT, DEFUALT_HEIHGT));
                }                
            }
        } catch (BackingStoreException ex) {
            LOGGER.error(ex, "Could not access preferences for window " + name);
        }
    }
    
    /**
     * Adds an OnCloseRequest handle to the window which will store the position, 
     * size and maximized status of the window. 
     * 
     * @param stage  the stage to read settings from
     * @param name   the name under which we store the settings
     */
    public static void applySaveOnCloseMethod(Stage stage, String name){
        stage.setOnCloseRequest( ev -> {
            try {
                Preferences stagePreferences = PREFERENCES.node(name);
                if( stage.isMaximized() ){
                    stagePreferences.putBoolean(WINDOW_MAXIMIZED, true);
                } else {
                    stagePreferences.putBoolean(WINDOW_MAXIMIZED, false);
                    stagePreferences.putDouble(WINDOW_X_POS, stage.getX());
                    stagePreferences.putDouble(WINDOW_Y_POS, stage.getY());
                    stagePreferences.putDouble(WINDOW_WIDTH, stage.getWidth());
                    stagePreferences.putDouble(WINDOW_HEIGHT, stage.getHeight());
                }
                stagePreferences.flush();
            } catch (final BackingStoreException ex) {
                LOGGER.error(ex, "Could not flush preferences for window " + name);
            } 
        });
    }
    
    private WindowSettingUtil(){
        instanceNotAllowed(getClass());
    }    
}