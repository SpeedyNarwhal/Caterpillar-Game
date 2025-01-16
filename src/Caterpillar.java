package src;

import java.awt.Color;
import java.util.Random;

import src.food.*;


public class Caterpillar {
    // All the fields have been declared public for testing purposes
    public Segment head;
    public Segment tail;
    public int length;
    public EvolutionStage stage;

    public MyStack<Position> positionsPreviouslyOccupied;
    public int goal;
    public int turnsNeededToDigest;


    public static Random randNumGenerator = new Random(1);


    // Creates a Caterpillar with one Segment. It is up to students to decide how to implement this.
    public Caterpillar(Position p, Color c, int goal) {
        // Make 1 length caterpillar
        this.head = new Segment(p, c);
        this.tail = this.head;
        this.tail.next = null;
        // Set the variables to default
        this.length = 1;
        this.stage = EvolutionStage.FEEDING_STAGE;
        this.goal = goal;
        // Initialize the positionsPreviouslyOccupied stack
        this.positionsPreviouslyOccupied = new MyStack<Position>();
    }

    public EvolutionStage getEvolutionStage() {
        return this.stage;
    }

    public Position getHeadPosition() {
        return this.head.position;
    }

    public int getLength() {
        return this.length;
    }


    // returns the color of the segment in position p. Returns null if such segment does not exist
    public Color getSegmentColor(Position p) {
        // Loop through each segment of the caterpillar, checking if one matches the position
        for(Segment segment = this.head; segment != null; segment = segment.next) {
            if(segment.position.equals(p)) {
                return segment.color;
            }
        }
        return null;
    }


    // Methods that need to be added for the game to work
    public Color[] getColors(){
        Color[] cs = new Color[this.length];
        Segment chk = this.head;
        for (int i = 0; i < this.length; i++){
            cs[i] = chk.color;
            chk = chk.next;
        }
        return cs;
    }

    public Position[] getPositions(){
        Position[] ps = new Position[this.length];
        Segment chk = this.head;
        for (int i = 0; i < this.length; i++){
            ps[i] = chk.position;
            chk = chk.next;
        }
        return ps;
    }


    // shift all Segments to the previous Position while maintaining the old color
    // the length of the caterpillar is not affected by this
    public void move(Position p) {
        // Check if head can go to the given position
        Position headPosition = this.getHeadPosition();
        if (!((Math.abs(headPosition.getX() - p.getX()) == 1 && headPosition.getY() == p.getY()) ||
                (Math.abs(headPosition.getY() - p.getY()) == 1 && headPosition.getX() == p.getX()))){
            throw new IllegalArgumentException("Tried to move to an invalid position.");
        }

        // Check if position is the same as a segment
        for(Position pos : this.getPositions()){
            if (pos.equals(p)) {
                this.stage = EvolutionStage.ENTANGLED;
                return;
            }
        }

        Position new_pos = p;
        // Pushes the tail's position on the positionsPreviouslyOccupied stack
        this.positionsPreviouslyOccupied.push(this.tail.position);
        // Swaps the positions of each segment to the position of the one in front
        for(Segment segment = this.head; segment != null; segment = segment.next){
            Position temp = segment.position;
            segment.position = new_pos;
            new_pos = temp;
        }

        // Check if caterpillar is growing
        if(this.turnsNeededToDigest > 0) {
            // Create a new segment at the tail end of the caterpillar
            this.tail.next = new Segment(this.positionsPreviouslyOccupied.pop(), GameColors.SEGMENT_COLORS[randNumGenerator.nextInt(5)]);
            this.tail = this.tail.next;
            this.tail.next = null;
            this.length++;
            this.turnsNeededToDigest--;

            // Check if butterfly
            if(this.length == this.goal){
                this.stage = EvolutionStage.BUTTERFLY;
                return;
            }
            // Check if cake energy is depleted
            if(turnsNeededToDigest == 0) {
                this.stage = EvolutionStage.FEEDING_STAGE;
            }
            else{
                this.stage = EvolutionStage.GROWING_STAGE;
            }
        }
        // If digestion is complete but still growing stage, reset to feeding stage
        else if(this.stage == EvolutionStage.GROWING_STAGE){
            this.stage = EvolutionStage.FEEDING_STAGE;
        }
    }

    // a segment of the fruit's color is added at the end
    public void eat(Fruit f) {
        // Set tail.next to the new segment, then set tail to the new element
        this.tail.next = new Segment(this.positionsPreviouslyOccupied.pop(), f.getColor());
        this.tail = this.tail.next;
        this.tail.next = null;
        this.length++;

        // Check if butterfly
        if(this.length == this.goal){
            this.stage = EvolutionStage.BUTTERFLY;
        }
    }


    // the caterpillar moves one step backwards because of sourness
    public void eat(Pickle p) {
        // Loop through segments and makes their positions the one of the next segment
        for(Segment segment = this.head; segment != this.tail; segment = segment.next){
            segment.position = segment.next.position;
        }
        // Updates the tail's position to the previously occupied position
        this.tail.position = this.positionsPreviouslyOccupied.pop();
    }


    // all the caterpillar's colors shuffle around
    public void eat(Lollipop lolly) {
        // Get colors in an array
        Color[] colors = getColors();
        // Shuffle array using the Fisher-Yates algorithm
        for(int i = colors.length - 1; i > 0; i--){
            int j = randNumGenerator.nextInt(i + 1);
            Color temp = colors[j];
            colors[j] = colors[i];
            colors[i] = temp;
        }

        // Set the colors of the segments to the new array
        int i = 0;
        for(Segment segment = this.head; segment != null && i <= this.length; segment = segment.next, i++){
            segment.color = colors[i];
        }
    }

    // brain freeze!!
    // It reverses and its (new) head turns blue
    public void eat(IceCream gelato) {
        // Initialize the 3 necessary variables
        Segment curr = this.head;
        Segment prev = null;
        Segment next;

        // Traverse through the segments
        while(curr != null){
            // Store the next segment
            next = curr.next;
            // Reverse the current segment's pointer
            curr.next = prev;
            // Move pointers 1 position ahead
            prev = curr;
            curr = next;
        }
        // Switch head and tail
        Segment temp = this.head;
        this.head = prev;
        this.tail = temp;

        // Turn head blue
        this.head.color = GameColors.BLUE;

        // Forget where he was
        this.positionsPreviouslyOccupied.clear();
    }


    // the caterpillar embodies a slide of Swiss cheese loosing half of its segments.
    public void eat(SwissCheese cheese) {
        // Store the new length (integer division rounded up)
        int newLength = this.length - (this.length / 2);
        // Get all the colors
        Color[] allColors = getColors();
        // Make a new list to store every other color
        Color[] newColors = new Color[newLength];
        // Add every other color to the newColors array (keeping head, then every other)
        for(int i = 0, j = 0; i < allColors.length; i++){
            if(i % 2 == 0){
                newColors[j] = allColors[i];
                j++;
            }
        }

        // Placeholder segment to traverse through the caterpillar
        Segment curr = this.head;
        // Temporary stack to add the positions in order (push to temp stack in reverse order, then pop/push into positionsPreviouslyOccupied in order)
        MyStack<Position> tempStack = new MyStack<Position>();
        // Traverse through the segments to add the deleted segments' positions to positionsPreviouslyOccupied
        for(int i = 0; i < this.length; i++){
            // Check if segment will be deleted to add it to positionsPreviouslyOccupied
            if(i >= newLength){
                tempStack.push(curr.position);
            }
            curr = curr.next;
        }
        // Go through tempStack and pop elements into positionsPreviouslyOccupied
        while(!tempStack.empty()){
            positionsPreviouslyOccupied.push(tempStack.pop());
        }

        // Reuse curr to iterate through the segments
        curr = this.head;
        // Traverse through the segments until we get to the new tail
        for(int i = 0; i < newLength && curr != null; i++){
            // Check if curr is the new tail
            if(i == newLength - 1){
                curr.next = null;
                this.tail = curr;
            }
            // Update the color of curr to its new color
            curr.color = newColors[i];
            // Iterate through segments
            curr = curr.next;
        }
        // Update length
        this.length = newLength;
    }



    public void eat(Cake cake) {
        // Set the stage to growing
        this.stage = EvolutionStage.GROWING_STAGE;

        // Variable to be used for turnsNeededToDigest
        int unusedEnergy = cake.getEnergyProvided();
        // Get all the segments' positions
        Position[] positions = this.getPositions();
        // Loop through cake energy level
        while(unusedEnergy > 0 && !this.positionsPreviouslyOccupied.empty()){
            Position nextPosition = this.positionsPreviouslyOccupied.peek();

            // Check if this position is already occupied
            for(Position pos : positions){
                if(pos.equals(nextPosition)){
                    // If position occupied, stop growth
                    this.turnsNeededToDigest = unusedEnergy;
                    return;
                }
            }

            // Use the position and energy
            unusedEnergy--;
            this.positionsPreviouslyOccupied.pop();

            // Create a new segment at the tail end of the caterpillar
            this.tail.next = new Segment(nextPosition, GameColors.SEGMENT_COLORS[randNumGenerator.nextInt(5)]);
            this.tail = this.tail.next;
            this.tail.next = null;
            this.length++;

            // Check if this is enough to evolve
            if(this.length == goal){
                this.stage = EvolutionStage.BUTTERFLY;
                return;
            }
        }
        // If all energy is consumed and the caterpillar hasn't evolved, update the stage
        if(unusedEnergy == 0){
            this.stage = EvolutionStage.FEEDING_STAGE;
        }
        else {
            // Update the turnsNeededToDigest if some energy left
            this.turnsNeededToDigest = unusedEnergy;
        }
    }



    // This nested class was declared public for testing purposes
    public class Segment {
        private Position position;
        private Color color;
        private Segment next;

        public Segment(Position p, Color c) {
            this.position = p;
            this.color = c;
        }

    }


    public String toString() {
        Segment s = this.head;
        String snake = "";
        while (s!=null) {
            String coloredPosition = GameColors.colorToANSIColor(s.color) +
                    s.position.toString() + GameColors.colorToANSIColor(Color.WHITE);
            snake = coloredPosition + " " + snake;
            s = s.next;
        }
        return snake;
    }



    public static void main(String[] args) {
        Position startingPoint = new Position(3, 2);
        Caterpillar gus = new Caterpillar(startingPoint, GameColors.GREEN, 10);

        System.out.println("1) Gus: " + gus);
        System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

        gus.move(new Position(3,1));
        gus.eat(new Fruit(GameColors.RED));
        gus.move(new Position(2,1));
        gus.move(new Position(1,1));
        gus.eat(new Fruit(GameColors.YELLOW));


        System.out.println("\n2) Gus: " + gus);
        System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

        gus.move(new Position(1,2));
        gus.eat(new IceCream());

        System.out.println("\n3) Gus: " + gus);
        System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

        gus.move(new Position(3,1));
        gus.move(new Position(3,2));
        gus.eat(new Fruit(GameColors.ORANGE));


        System.out.println("\n4) Gus: " + gus);
        System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

        gus.move(new Position(2,2));
        gus.eat(new SwissCheese());

        System.out.println("\n5) Gus: " + gus);
        System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

        gus.move(new Position(2, 3));
        gus.eat(new Cake(4));

        System.out.println("\n6) Gus: " + gus);
        System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);
    }
}