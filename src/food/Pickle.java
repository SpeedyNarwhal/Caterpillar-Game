package src.food;

import src.Caterpillar;


public class Pickle extends FoodItem{
	
	public void accept(Caterpillar c) {
		c.eat(this);
	}

}
