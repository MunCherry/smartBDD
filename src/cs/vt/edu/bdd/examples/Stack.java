/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.vt.edu.bdd.examples;

/**
 *
 * @author akshaymaloo
 */

// Stack.java: stack implementation
public class Stack {

    private int maxStack;
    private int emptyStack;
    private int top;
    private int[] items;

    public Stack(int size) {
        maxStack = size;
        emptyStack = -1;
        top = emptyStack;
        items = new int[maxStack];
    }
    
    public Stack() {
        maxStack = 100;
        emptyStack = -1;
        top = emptyStack;
        items = new int[maxStack];
    }

    public void push(int input) {
        items[++top] = input;
        //return true;
    }

    public int pop() {
        return items[top--];
    }

    public boolean full() {
        return top + 1 == maxStack;
    }

    public boolean isEmpty() {
        return top == emptyStack;
    }
    
    public int size() {
        return top+1;
    }
    
    public static void main(String args[]) {
        
        //degug code
        //Stack testS = new Stack();
        Stack testStack = new Stack(10);
        
        testStack.push(1);
//        testS.push(2);
//        testS.push(3);
//        testS.push(4);
        
        System.out.println("Size: " + testStack.size());
        System.out.println(testStack.pop());
        //System.out.println(testS.pop());
        
    }
}