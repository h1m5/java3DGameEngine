package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
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

        Light light = new Light(new Vector3f(0, 10, 0), new Vector3f(1, 1, 1));

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        Terrain terrain1 = new Terrain(0, 0, loader, texturePack, blendMap);
        Terrain terrain2 = new Terrain(1, 0, loader, texturePack, blendMap);
        Terrain terrain3 = new Terrain(1, 1, loader, texturePack, blendMap);
        Terrain terrain4 = new Terrain(0, 1, loader, texturePack, blendMap);

        //player
        RawModel bunnyModel = OBJLoader.loadObjModel("bunny", loader);
        TexturedModel stanfordBuny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));

        Player player = new Player(stanfordBuny, new Vector3f(0, 0, 0), 0, 0, 0, 0.2f);

        Camera camera = new Camera(player);

        List<Entity> trees = new ArrayList<Entity>();
        Random random = new Random();
        for(int i = 0; i<200; i++){
            float x = random.nextFloat() * 100 - 50;
            float y = 0;
            float z = random.nextFloat() * -300;
            trees.add(new Entity(staticModel, new Vector3f(x,y,z), 0, random.nextFloat()*180f, 0f, 1f));
        }

        List<Entity> grass = new ArrayList<Entity>();
        for(int i = 0; i<200; i++){
            float x = random.nextFloat() * 100 - 50;
            float y = 0;
            float z = random.nextFloat() * -300;
            grass.add(new Entity(staticGrassModel, new Vector3f(x, y, z), 0, random.nextFloat() * 180f, 0f, 0.5f));
        }

        MasterRenderer renderer = new MasterRenderer();
        while(!Display.isCloseRequested()){
            entity.increaseRotation(0, 1, 0);
            camera.move();
            player.move();

            renderer.processEntity(player);
            renderer.processTerrain(terrain1);
            renderer.processTerrain(terrain2);
            renderer.processTerrain(terrain3);
            renderer.processTerrain(terrain4);
//            renderer.processEntity(entity);

            for (Entity e:trees){
                renderer.processEntity(e);
            }
            for (Entity e:grass){
                renderer.processEntity(e);
            }

            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
