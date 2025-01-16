package src.food;

import src.Caterpillar;


public class Lollipop extends FoodItem{
	
	public void accept(Caterpillar c) {
		c.eat(this);
	}

}
