package fr.utln.jmonkey.Projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.util.BufferUtils;
import com.jme3.scene.VertexBuffer;

//Gestion des lunes de la meme facon que les planetes mais les informations sont rentrées à la main et pas avec l'api
public class Lune implements Corp{
	//A peut pret les meme attributs et methodes que les planetes
	//Mais pas de possibilité d'héritage entre lune et planete
	private String name;
	private double revolution;
	private float inclinaisonOrbitale;
	private float size;
	private double rayonMoyen;
	private double distance;
	private double masse;
	private double gravite;
	private double temperatureK;

	private double demiGrandAxe;
	private double excentricite;
	private double periodeOrbitale;

	private Geometry orbite;
	private Geometry planet;
	private Node root;
	private Node node;

	public Lune(String name, double revolution, float inclinaisonOrbitale, float size, double distance, double rayonMoyen, double masse, double temperatureK, double gravite, double demiGrandAxe, double excentricite, double periodeOrbitale, AssetManager assetManager){
		this.name = name;
		this.revolution = revolution;;
		this.inclinaisonOrbitale = inclinaisonOrbitale;
		this.size = size;
		this.distance = distance;

		this.temperatureK=temperatureK;
		this.gravite=gravite;
		this.masse=masse;
		this.rayonMoyen=rayonMoyen;

		this.demiGrandAxe = demiGrandAxe;
		this.excentricite = excentricite;
		this.periodeOrbitale = periodeOrbitale;
		
		initNodes();
		initPlanet(assetManager);
		initOrbite(assetManager);
	}

	private void initPlanet(AssetManager assetManager){
		System.out.println(name + " " + size);
		Sphere sphere = new Sphere(32, 32, size);
		sphere.setTextureMode(Sphere.TextureMode.Projected);
		planet = new Geometry(name, sphere);
		
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setTexture("DiffuseMap", assetManager.loadTexture("Planets/"+ name +".jpg"));

		planet.setMaterial(mat);
		node.attachChild(planet);
	}

	private void initNodes(){
		node = new Node(name + "Node");
		root = new Node(name + "Root");

		root.attachChild(node);
		node.setLocalTranslation((float)distance, 0, 0);

		float inclinationRadians = FastMath.DEG_TO_RAD * inclinaisonOrbitale;
		// System.out.println(name + " " + inclinaisonOrbitale + " " + inclinationRadians);
		node.rotate(-FastMath.HALF_PI, 0, 0);
		root.rotate(inclinationRadians, 0f, 0f);
	}

	public void rotate(double time){
		float angle = FastMath.DEG_TO_RAD * (float)time * 360f / ((float)revolution*24f*3600f*1000f);
		root.rotate(0f, angle, 0f);
		// System.out.println("Rotate " + name + " " + angle);
	}

	public void initOrbite(AssetManager assetManager){
		int samples = 128;
		float a = (float)(Planet.RAYON_MOYEN_TERRE * demiGrandAxe/Planet.DEMI_GRAND_AXE_TERRE);
		if(name.equals("Lune")){
			a /= 10;
		}
		if(!name.equals("Lune")){
			a = (float)distance;
		}
		if(name.equals("Europa")){
			a *= 1.01;
		}
		System.out.println(name + " orbite " + demiGrandAxe + " " + a);

		float demiPetitAxe = a * (float) Math.sqrt(1 - Math.pow(excentricite, 2d));
		Mesh mesh = new Mesh();
		Vector3f[] vertices = new Vector3f[samples + 1];
		int[] indices = new int[samples * 2];

		for (int i = 0; i < samples; i++) {
			float angle = (float) (2 * Math.PI * i / samples);
			float x = (float) a * (float) Math.cos(angle) - (float)(excentricite)*a;
			float y = demiPetitAxe * (float) Math.sin(angle);

			vertices[i] = new Vector3f(x, y, 0);
			indices[i * 2] = i;
			indices[i * 2 + 1] = (i + 1) % samples;
		}

		vertices[samples] = vertices[0]; 

		mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		mesh.setBuffer(VertexBuffer.Type.Index, 2, BufferUtils.createIntBuffer(indices));
		mesh.setMode(Mesh.Mode.Lines);
		mesh.updateBound();
		mesh.updateCounts();

		orbite = new Geometry("Ellipse"+name, mesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", assetManager.loadTexture("Planets/" + name + ".jpg"));
		orbite.setMaterial(mat);
		orbite.rotate(-FastMath.HALF_PI, 0, 0);
		root.attachChild(orbite);
	}

	public String getName(){
		return name;
	}

	public Node getNode(){
		return node;
	}

	public Node getRoot(){
		return root;
	}

	@Override
	public Geometry getPlanet() {
		return planet;
	}

	@Override
	public double size(){
		return size;
	}

	public double getRayonMoyen(){
		return rayonMoyen;
	}

	public double getMasse() {
		return masse;
	}

	public double getGravite() {
		return gravite;
	}

	public double getTemperatureK() {
		return temperatureK;
	}

	public Geometry getOrbite() {
		return orbite;
	}
}
