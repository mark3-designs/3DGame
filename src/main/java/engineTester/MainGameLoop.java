package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {   

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		//terrain texture//////
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		
		/////////////////////
		ModelData treeData = OBJFileLoader.loadOBJ("Tree");
		ModelData fernData = OBJFileLoader.loadOBJ("fern");
		
		RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), 
				treeData.getNormals(), treeData.getIndices());
		RawModel fernModel = loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), 
				fernData.getNormals(), fernData.getIndices());
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		
		fernTextureAtlas.setNumberOfRows(2);
		
		TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("TreeTexture")));
		TexturedModel fern = new TexturedModel(fernModel, fernTextureAtlas);
		
		fern.getTexture().setHasTransparency(true);	

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		
		Light light = new Light(new Vector3f(0,10000,-10000),new Vector3f(1,1,1));
		
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");

		
		MasterRenderer renderer = new MasterRenderer();
		
		ModelData bunnyData = OBJFileLoader.loadOBJ("stanfordBunny");
		
		RawModel bunnyModel = loader.loadToVAO(bunnyData.getVertices(), bunnyData.getTextureCoords(), 
				bunnyData.getNormals(), bunnyData.getIndices());
		
		TexturedModel bunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("white")));
		
		Player player = new Player(bunny, new Vector3f(100, 5, -150), 0, 100, 0, .5f);
		Camera camera = new Camera(player, terrain);	
		
		for(int i=0;i<500;i++){
			
			float x = random.nextFloat()*750+25;
			float z = random.nextFloat() * -750-25;
			
			float y = terrain.getHeightOfTerrain(x, z);
			
			int pick = random.nextInt(3);
			
			if(pick==0){
				entities.add(new Entity(tree, new Vector3f(x, y, z),0,0,0,random.nextFloat()*10+2));
			}else if(pick==1){
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z),0,0,0,0.9f));
			}else{
			
			}
		}
		
		
		
		while(!Display.isCloseRequested()){
			player.move(terrain);
			camera.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for(Entity entity:entities){
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
