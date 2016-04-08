package engineTester;

import com.sun.org.apache.xpath.internal.operations.Bool;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by HimsDLee on 29/02/16.
 */
public class MainGameLoop {
    public static List<Entity> generateMultipleModels(String modelName, String textureName, int numberOfTextureRows,
                                                      Loader loader, List<Terrain> terrains,
                                                      float scale, boolean hasTransparency, boolean useFakeLighting,
                                                      float shineDamper, float reflectivity){
        RawModel model = OBJLoader.loadObjModel(modelName, loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
        TexturedModel texturedModel = new TexturedModel(model, texture);
        texture.setNumberOfRows(numberOfTextureRows);
        texture.setShineDamper(shineDamper);
        texture.setReflectivity(reflectivity);
        texture.setHasTransparency(hasTransparency);
        texture.setUseFakeLighting(useFakeLighting);

        Random random = new Random();
        int min = -300; int max = 300;
        int n = min - max + 1;
        List<Entity> entities = new ArrayList<Entity>();
        for(int i = 0; i<500; i++){
            float x = (float)random.nextInt((max - min) + 1) + min;
            float z = (float)random.nextInt((max - min) + 1) + min;
            float y = 0;
            if(x >= 0 && z >= 0){
                y = terrains.get(0).getHeightOfTerrain(x, z);
            } else if (x < 0 && z >= 0){
                y = terrains.get(1).getHeightOfTerrain(x, z);
            } else if (x <= 0 && z <= 0){
                y = terrains.get(2).getHeightOfTerrain(x, z);
            } else if (x >= 0 && z < 0){
                y = terrains.get(3).getHeightOfTerrain(x, z);
            }
            //entities.add(new Entity(texturedModel, new Vector3f(x, y, z), 0, random.nextFloat() * 180f, 0f, scale));
            entities.add(new Entity(texturedModel, random.nextInt(numberOfTextureRows*numberOfTextureRows), new Vector3f(x,y,z), 0, random.nextFloat() * 180f, 0f, scale));
        }
        return entities;
    }

    public static TexturedModel generateSingleTexturedModel(String modelName, String textureName, Loader loader, boolean hasTransparency, boolean useFakeLighting,
                                                            float shineDamper, float reflectivity){
        RawModel model = OBJLoader.loadObjModel(modelName, loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture(textureName));
        texture.setUseFakeLighting(useFakeLighting);
        texture.setHasTransparency(hasTransparency);
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(shineDamper);

        return new TexturedModel(model, texture);
    }

    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        //treeModel
        ModelData data = OBJFileLoader.loadOBJ("tree");
//        RawModel model = OBJLoader.loadObjModel("tree", loader);
        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        ModelTexture texture = new ModelTexture(loader.loadTexture("tree"));
        texture.setShineDamper(30);
        texture.setReflectivity(1);
        TexturedModel staticModel = new TexturedModel(model, texture);
        Entity entity = new Entity(staticModel, new Vector3f(0.0f, 0f, -5), 0, 0, 0, 0.1f);

        //grassModel
        RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
        ModelTexture grassTexture = new ModelTexture(loader.loadTexture("grassTexture"));
        TexturedModel staticGrassModel = new TexturedModel(grassModel, grassTexture);
        staticGrassModel.getTexture().setHasTransparency(true);
        staticGrassModel.getTexture().setUseFakeLighting(true);

        //lights
        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(0, 100, 0), new Vector3f(0.3f, 0.3f, 0.3f));
        Light light1 = new Light(new Vector3f(10, 15, 0), new Vector3f(1,1,0), new Vector3f(1, 0.01f, 0.002f));
        Light light2 = new Light(new Vector3f(-10, 15, 0), new Vector3f(1,0,1), new Vector3f(1, 0.01f, 0.002f));
        lights.add(sun);
        lights.add(light1);
        lights.add(light2);

        List<Entity> entities = new ArrayList<Entity>();
        TexturedModel lamp = generateSingleTexturedModel("lamp", "lamp", loader, false, true, 20, 1);
        entities.add(new Entity(lamp, new Vector3f(10,0,0),0,0,0,1));
        entities.add(new Entity(lamp, new Vector3f(-10,0,0),0,0,0,1));

        //terrain
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        Terrain terrain1 = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");
        Terrain terrain2 = new Terrain(-1, 0, loader, texturePack, blendMap, "heightmap");
        Terrain terrain3 = new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap");
        Terrain terrain4 = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");

        List<Terrain> terrains = new ArrayList<Terrain>();
        terrains.add(terrain1);
        terrains.add(terrain2);
        terrains.add(terrain3);
        terrains.add(terrain4);
        //player
        RawModel bunnyModel = OBJLoader.loadObjModel("bunny", loader);
        TexturedModel stanfordBuny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));

        Player player = new Player(stanfordBuny, new Vector3f(0, 0, 0), 0, 0, 0, 0.2f);

        Camera camera = new Camera(player);

        List<Entity> trees = generateMultipleModels("lowPolyTree", "lowPolyTree", 1, loader, terrains, 0.2f, false, false, 20, 0);
        List<Entity> grass = generateMultipleModels("grassModel", "grassTexture", 1, loader, terrains, 0.5f, true, true, 0, 0);
        List<Entity> ferns = generateMultipleModels("fern", "fern", 2, loader, terrains, 0.5f, true, true, 0, 0);

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("lowPolyTree"), new Vector2f(0.5f, 0.5f), new Vector2f(0.1f, 0.1f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MasterRenderer renderer = new MasterRenderer();
        while(!Display.isCloseRequested()){
            entity.increaseRotation(0, 1, 0);
            camera.move();

            if(player.getPosition().x >= 0 && player.getPosition().z >= 0){
                player.move(terrain1);
            } else if (player.getPosition().x < 0 && player.getPosition().z >= 0){
                player.move(terrain2);
            } else if (player.getPosition().x <= 0 && player.getPosition().z <= 0){
                player.move(terrain3);
            } else if (player.getPosition().x >= 0 && player.getPosition().z < 0){
                player.move(terrain4);
            }

            renderer.processEntity(player);
            renderer.processTerrain(terrain1);
            renderer.processTerrain(terrain2);
            renderer.processTerrain(terrain3);
            renderer.processTerrain(terrain4);
//            renderer.processEntity(entity);

            for (Entity e: entities){
                renderer.processEntity(e);
            }

            for (Entity e:trees){
                renderer.processEntity(e);
            }
            for (Entity e:grass){
                renderer.processEntity(e);
            }

            for (Entity e:ferns){
                renderer.processEntity(e);
            }

            renderer.render(lights, camera);
            guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }

        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
