package engineTester;

import com.sun.org.apache.xpath.internal.operations.Bool;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.*;
import models.RawModel;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

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
        texture.setShineDamper(20);
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
        Light sun = new Light(new Vector3f(0, 100, 0), new Vector3f(0.6f, 0.6f, 0.6f));
        Light light1 = new Light(new Vector3f(10, 15, 0), new Vector3f(1,1,1), new Vector3f(1, 0.01f, 0.002f));
        Light light2 = new Light(new Vector3f(-10, 15, 0), new Vector3f(1,1,1), new Vector3f(1, 0.01f, 0.002f));
        lights.add(sun);
        lights.add(light1);
        lights.add(light2);

        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> normalMapEntities = new ArrayList<Entity>();

        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
                            new ModelTexture(loader.loadTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);

        normalMapEntities.add(new Entity(barrelModel, new Vector3f(10, 10, 10), 0, 0, 0, 1f));

        TexturedModel lamp = generateSingleTexturedModel("lamp", "lamp", loader, false, true, 20, 1);
        Entity lampEntity = new Entity(lamp, new Vector3f(10,0,0),0,0,0,1);
        entities.add(lampEntity);
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
        RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
        TexturedModel stanfordBuny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));

        Player player = new Player(stanfordBuny, new Vector3f(0, 0, 0), 0, 0, 0, 0.2f);
        entities.add(player);

        Camera camera = new Camera(player);

        List<Entity> trees = generateMultipleModels("lowPolyTree", "lowPolyTree", 1, loader, terrains, 0.2f, false, false, 50, 0);
        List<Entity> grass = generateMultipleModels("grassModel", "grassTexture", 1, loader, terrains, 0.5f, true, true, 0, 0);
        List<Entity> ferns = generateMultipleModels("fern", "fern", 2, loader, terrains, 0.5f, true, true, 0, 0);

        entities.addAll(trees);
        entities.addAll(grass);
        entities.addAll(ferns);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MasterRenderer renderer = new MasterRenderer(loader);


        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
        List<WaterTile> waters = new ArrayList<WaterTile>();
        WaterTile water = new WaterTile(75, -75, 2);
        waters.add(water);


        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture reflection = new GuiTexture(fbos.getReflectionTexture(), new Vector2f(-0.6f, 0.6f), new Vector2f(0.2f, 0.2f));
        GuiTexture refraction = new GuiTexture(fbos.getRefractionTexture(), new Vector2f(0.6f, 0.6f), new Vector2f(0.2f, 0.2f));
//        guis.add(reflection);
//        guis.add(refraction);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

        while(!Display.isCloseRequested()){
            entity.increaseRotation(0, 1, 0);
            camera.move();

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            if(player.getPosition().x >= 0 && player.getPosition().z >= 0){
                player.move(terrain1);
            } else if (player.getPosition().x < 0 && player.getPosition().z >= 0){
                player.move(terrain2);
            } else if (player.getPosition().x <= 0 && player.getPosition().z <= 0){
                player.move(terrain3);
            } else if (player.getPosition().x >= 0 && player.getPosition().z < 0){
                player.move(terrain4);
            }

            picker.update();
//            System.out.println(picker.getCurrentRay());
            if(Mouse.isButtonDown(0)) {
                Vector3f terrainPoint = picker.getCurrentTerrainPoint();
                if (terrainPoint != null) {
                    lampEntity.setPosition(terrainPoint);
                    light1.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
                }
            }

            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()));
            camera.getPosition().y += distance;
            camera.invertPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));

            fbos.unbindCurrentFrameBuffer();
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100));
            waterRenderer.render(waters, camera, sun);
            guiRenderer.render(guis);


            DisplayManager.updateDisplay();
        }

        fbos.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
