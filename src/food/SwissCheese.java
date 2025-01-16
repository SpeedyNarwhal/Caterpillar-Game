package src.food;

import src.Caterpillar;


public class SwissCheese extends FoodItem{
	
	public void accept(Caterpillar c) {
		c.eat(this);
	}

}
