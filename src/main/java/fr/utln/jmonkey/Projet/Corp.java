package fr.utln.jmonkey.Projet;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

//Interface corp pour contenir etoile, planete et lune
public interface Corp {
	public String getName();
	public Geometry getPlanet();
	public Node getRoot();
	public Node getNode();
	public double size();
	public double getMasse();
	public double getTemperatureK();
	public double getRayonMoyen();
	public double getGravite();
	public Geometry getOrbite();
}