package jp.co.worksap.global;

import java.util.LinkedList;
import java.util.jar.JarException;

import javax.lang.model.element.Element;

import sun.org.mozilla.javascript.internal.ast.ThrowStatement;



public class ImmutableQueue<E> {
	
    
	private class Node<E>{
		E item;
		Node<E> next;
		
		Node(E item, Node<E> next){
			this.item = item;
			this.next = next;
		}
	}
	
	
    private Node<E> first;
    private Node<E> last;
    private int size;
		
    public ImmutableQueue(){
    	first = null;
    	last = null;
    	size = 0;
    }
    
    /**
     * Add an element to the last of the queue.
     * @param e
     * @return ImmutableQueue<E>
     */
    
    public ImmutableQueue<E> addLast(E e){
    	
    	Node<E> newNode = new Node<E>(e, null);
    	
    	if(first == null){
    		first = newNode;
    		last = newNode;
    	}else{
    		last.next = newNode;
    		last = newNode;
    	}
    	
    	size++;
    	return this;
    	
    }
    
    
    
    /**
     * Clone a new ImmutableQueue<E> object.
     * @param element
     * @return new clone ImmutableQueue<E>
     */
    public ImmutableQueue<E> Clone(ImmutableQueue<E> element){
    	if(element == null)
    		throw new IllegalArgumentException("Usage: java " + 
    	    ImmutableQueue.class.getName());
    	
    	ImmutableQueue<E> clone = new ImmutableQueue<E>();
    	
    	for(Node<E> e = element.first; e != null; e = e.next)
    		clone = clone.addLast(e.item);
    	
    	return clone;
    	
    }
    
    /**
     * Return the queue which delete the first element.
     * @return
     */
    
    public ImmutableQueue<E> removeFirst(){
    	if(first == null)
    		throw new java.util.NoSuchElementException("Usage: java " + 
    	    ImmutableQueue.class.getName());
    	

    	this.first = this.first.next;
    	
    	return this;
    }
    
    /**
     * Return the queue that adds an item into the tail of this queue without modifying this queue.
     * <pre>
     * e.g.
     * When this queue represents the queue (2, 1, 2, 2, 6) and we enqueue the value 4 into this queue.
     * this method returns a new queue (2, 1, 2, 2, 6, 4)
     * and this object still represents the queue (2, 1, 2, 2, 6, 4)
     * </pre>
     * If the element e is null, throws the IllegalArgumentException.
     * @param e
     * @return
     * @throws IllegalArgumentException
     */

    
    public ImmutableQueue<E> enqueue(E e) throws IllegalArgumentException{
    	if(e == null)
    		throw new IllegalArgumentException("Usage: java " + 
    	    ImmutableQueue.class.getName());
    		
    	return addLast(e);
    }
    
    /**
     * Return the queue that removes the object at the head of this queue without modifying this queue.
     * <pre>
     * e.g.
     * When this queue represents the queue (7, 1, 3, 3, 5, 1).
     * this method returns a new queue (1, 3, 3, 5, 1)
     * and this object still represents the queue (7, 1, 3, 3, 5, 1).
     * </pre>
     * If this queue is empty, throws java.util
     */
    public ImmutableQueue<E> dequeue(){
    	if(first == null)
    		throw new java.util.NoSuchElementException("Usage: java " + 
    		ImmutableQueue.class.getName());
    	
    	ImmutableQueue<E> newQueue = Clone(this);
    	newQueue.removeFirst();
    	
    	return newQueue;
        
    }
    
    /**
     * Looks at the object which is the head of this queue without removing it from the queue
     * <pre>
     * e.g.
     * When this queue represents the queue (7, 1, 3, 3, 5, 1)
     * this method returns 7 and this object still represents the queue (7, 1, 3, 3, 5, 1)
     * </pre>
     * @return
     * @throws java.util.NoSuchElementException
     */
    public E peek(){
    	if(first == null)
    		throw new java.util.NoSuchElementException("Usage: java " + 
    	    ImmutableQueue.class.getName());
    	return first.item;
    }
    
    /**
     * Returns the number of objects in this queue.
     * @return
     */
    public int size(){
    	return size;
    }
}
