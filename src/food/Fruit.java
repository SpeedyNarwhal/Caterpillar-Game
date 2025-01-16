package src.food;

import src.Caterpillar;

import java.awt.*;


public class Fruit extends FoodItem {
	private Color color;
	
	public Fruit(Color c) {
		this.color = c;
	}
	
	public Color getColor() {
		return this.color;
	}

	
	public void accept(Caterpillar c) {
		c.eat(this);
	}
}
