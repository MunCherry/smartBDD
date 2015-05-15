package cs.vt.edu.bdd.examples;

public class Queue {
	public int size;
	
	public Queue(int _size){
		size = _size;
	}
	
	public void dequeue(){
		size = size - 1;
	}
	
	public void enqueue(int elem){
		size = size + 1;
	}

}
