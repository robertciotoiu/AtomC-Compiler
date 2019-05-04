package main;

public class Counter {
	  private final int i;
	  private final boolean bool;

	  public Counter(int counter, boolean bool) {
	    this.i = counter;
	    this.bool = bool;
	  }

	  public int getCounter() { return i; }
	  public boolean getBool() { return bool; }

//	  @Override
//	  public boolean equals(Object o) {
//	    if (!(o instanceof Pair)) return false;
//	    Pair pairo = (Pair) o;
//	    return this.left.equals(pairo.getLeft()) &&
//	           this.right.equals(pairo.getRight());
//	  }
}
