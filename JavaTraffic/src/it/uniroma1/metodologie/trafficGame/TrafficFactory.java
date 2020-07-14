package it.uniroma1.metodologie.trafficGame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

public class TrafficFactory implements EntityFactory{
	
	private Entity player1;
	
	private Entity player2;
	
	@Spawns("player")
	public Entity getPlayer1(SpawnData data) {
		if(data.get("player").equals("player1")) {
			if(player1 == null)
				player1 = FXGL.entityBuilder().viewWithBBox(new Rectangle(250,250, Color.YELLOW)).build();
			return player1;
		}
		if(player2 == null)
			player2 = FXGL.entityBuilder().viewWithBBox(new Rectangle(250,250, Color.RED)).build();
		return player2;
	}
	
	@Spawns("car")
	public Entity getCar(SpawnData data) {
		//TODO a method that spawns cars, tirs and motorbikes based on the data passed (heigh, wdth, direction...)
	}
	
}
