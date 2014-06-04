package jp.co.worksap.global;

public class QueueTest {
	
	
	public static void main(String[] args){
		ImmutableQueue<Integer> queue = new ImmutableQueue();
		
		for(int i = 1; i <= 5; ++i)
			queue = queue.enqueue(i);
		
		System.out.println("size = " + queue.size());
		int size = queue.size();
		for(int i = 0; i < size; ++i){
			System.out.println("i = " + i + " " + queue.peek());
			queue = queue.dequeue();
			
		}
			
		
	}

}
