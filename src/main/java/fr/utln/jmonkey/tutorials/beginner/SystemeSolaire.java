package fr.utln.jmonkey.tutorials.beginner;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

public class SystemeSolaire extends SimpleApplication {
	private List<Node> rootSoleil;
	private List<Node> nodes;
	private List<Node> lunes;
	
	// private float angleTerre = 0;
	// private float angleLune = 0;

	public static void main(String[] args) {
		SystemeSolaire app = new SystemeSolaire();
		app.start();
	}

	@Override
	public void simpleInitApp(){
		ColorRGBA colors[] = {ColorRGBA.Yellow, ColorRGBA.Orange, ColorRGBA.DarkGray, ColorRGBA.Blue, ColorRGBA.Red, ColorRGBA.Gray, ColorRGBA.LightGray, ColorRGBA.Blue, ColorRGBA.Blue, ColorRGBA.Gray};

		rootSoleil = new ArrayList<>();
		nodes = new ArrayList<>();
		lunes = new ArrayList<>();

		nodes.add(new Node("SoleilNode"));
		nodes.add(new Node("MercureNode"));
		nodes.add(new Node("VenusNode"));
		nodes.add(new Node("TerreNode"));
		nodes.add(new Node("MarsNode"));
		nodes.add(new Node("JupiterNode"));
		nodes.add(new Node("SaturneNode"));
		nodes.add(new Node("UranusNode"));
		nodes.add(new Node("NeptuneNode"));

		rootSoleil.add(new Node("SoleilR"));
		rootSoleil.add(new Node("MercureR"));
		rootSoleil.add(new Node("VenusR"));
		rootSoleil.add(new Node("TerreR"));
		rootSoleil.add(new Node("MarsR"));
		rootSoleil.add(new Node("JupiterR"));
		rootSoleil.add(new Node("SaturneR"));
		rootSoleil.add(new Node("UranusR"));
		rootSoleil.add(new Node("NeptuneR"));

		lunes.add(new Node("TerreL"));
		lunes.add(new Node("MarsL1"));
		lunes.add(new Node("MarsL2"));
		lunes.add(new Node("JupiterL1"));
		lunes.add(new Node("JupiterL2"));

		for(int i=0; i<rootSoleil.size(); i++){
			Node n = nodes.get(i);
			rootNode.attachChild(rootSoleil.get(i));
			rootSoleil.get(i).attachChild(n);
			Geometry s = createSphere(n.getName().substring(0, n.getName().length()-1), 1f, colors[i]);
			n.attachChild(s);
			s.setLocalTranslation(i*5, 0, 0);
		}

		nodes.get(3).attachChild(lunes.get(0));
		nodes.get(4).attachChild(lunes.get(1));
		nodes.get(4).attachChild(lunes.get(2));
		nodes.get(5).attachChild(lunes.get(3));
		nodes.get(5).attachChild(lunes.get(4));
		for(Node l : lunes){
			Geometry s = createSphere(l.getName().substring(l.getName().length()-1), 0.1f, ColorRGBA.Gray);
			l.attachChild(s);
			s.setLocalTranslation(2, 0, 0);
		}
		
	}

	@Override
	public void simpleUpdate(float tpf) {
		// angleTerre += tpf;
		// angleLune += tpf * 3;

		// soleilNode.rotate(0, tpf/4, 0);
		// terreNode.rotate(0,tpf/2,0);

		//terreNode.setLocalTranslation(FastMath.cos(angleTerre), 0,FastMath.sin(angleTerre));
		//terreNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		//luneNode.setLocalTranslation(FastMath.cos(angleLune), 0,FastMath.sin(angleLune));
	}

	private Geometry createSphere(String name, float radius, ColorRGBA color) {
		Sphere sphere = new Sphere(32, 32, radius);
		Geometry geom = new Geometry(name, sphere);
		
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		
		geom.setMaterial(mat);
		return geom;
	}

	@Override
	public void start() {
		AppSettings settings = new AppSettings(true);
		settings.setWidth(1900); // Largeur de la fenêtre
		settings.setHeight(1000); // Hauteur de la fenêtre
		settings.setFullscreen(false); // Désactive le mode plein écran
		setSettings(settings);
		super.start();
	}
}