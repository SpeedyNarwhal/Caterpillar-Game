package src.food;

import src.Caterpillar;


public class IceCream extends FoodItem{
	
	public void accept(Caterpillar c) {
		c.eat(this);
	}

}
