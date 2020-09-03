package it.uniroma1.metodologie.trafficGame.components;

import java.util.List;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class CrossRoadComponent extends Component{
	
	private int carsOnCrossroad;
	
	public void addCar() { carsOnCrossroad ++; }
	public void subCar() {carsOnCrossroad --; }
	
	public boolean isFree() { return carsOnCrossroad == 0; }
}
