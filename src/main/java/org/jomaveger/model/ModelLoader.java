package org.jomaveger.model;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.jomaveger.model.bsp.Quake3BSP;
import org.jomaveger.model.md2.ModelQuake2;
import org.jomaveger.model.md3.ModelQuake3;
import org.jomaveger.util.TextFile;

/**
 * @author jmvegas.gertrudix
 */
public enum ModelLoader {
    
    INSTANCE();
    
    private static final Logger LOGGER = Logger.getLogger(ModelLoader.class);
    
    private ModelLoader() {
    }
    
    public ModelType extractModelType(String filename) {
        if (filename == null)
            throw new RuntimeException("filename must not be null");        
        
        if (filename.endsWith(".bsp"))
            return ModelType.BSP;
        
        if (filename.endsWith(".md2"))
            return ModelType.MD2;
        
        if (filename.endsWith(".md3"))
            return ModelType.MD3;
        
        if (filename.endsWith(".md5mesh"))
            return ModelType.MD5;
        
        throw new RuntimeException("Unknown model file \"" + filename + "\".");
    }
    
    public ModelQuake2 loadMD2Model(String modelFile, String skinFile, ModelType modelType) {
        if (modelType == ModelType.MD2) {
            ModelQuake2 model = new ModelQuake2(skinFile);
            model.load(modelFile);
            return model;
        } else {
            LOGGER.info("Asking for loading a MD2Model with the wrong modelType: " + modelType.name());
            throw new RuntimeException("Asking for loading a MD2Model with the wrong modelType: " + modelType.name());
        }
    }
    
    public ModelQuake3 loadMD3Model(String modelPath, String modelName, String gunName, ModelType modelType) {
        if (modelType == ModelType.MD3) {
            ModelQuake3 model = new ModelQuake3();
            try {
                model.load(modelPath, modelName);
                model.loadWeapon(modelPath, gunName);
            } catch (IOException ex) {
                LOGGER.info("It was not possible to load the md2 model. The application will shut down.");
                System.exit(-1);
            }
            return model;
        } else {
            LOGGER.info("Asking for loading a MD3Model with the wrong modelType: " + modelType.name());
            throw new RuntimeException("Asking for loading a MD3Model with the wrong modelType: " + modelType.name());
        }
    }
    
    public Quake3BSP loadQuake3BSPModel(String configFile, ModelType modelType) {
        if (modelType == ModelType.BSP) {
            Quake3BSP level = new Quake3BSP();
            try {
                BufferedReader reader = TextFile.openFile("Config.ini");
                String strLine = null;
                String nameLevel = null;
                String gammaFactor = null;

                while ((strLine = reader.readLine()) != null) {
                    if (strLine.contains("[Level]")) {
                        nameLevel = strLine.substring(8);
                    }

                    if (strLine.contains("[Gamma]")) {
                        gammaFactor = strLine.substring(8);
                    }
                }

                boolean loaded = level.loadBSP(nameLevel, gammaFactor);
                if (!loaded) {
                    LOGGER.info("It was not possible to load the Quake 3 BSP level: File Map does not exist. The application will shut down.");
                    System.exit(-1);
                }
            } catch (IOException ex) {
                LOGGER.info("It was not possible to load the Quake3BSP model. The application will shut down.");
                System.exit(-1);
            }
            return level;
        } else {
            LOGGER.info("Asking for loading a Quake3BSP with the wrong modelType: " + modelType.name());
            throw new RuntimeException("Asking for loading a Quake3BSP with the wrong modelType: " + modelType.name());
        }
    }
}
