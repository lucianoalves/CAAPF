/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.test;

import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * @author luciano
 */
public class Solution {

    private final LinkedList<Character> queue;
    private final Stack<Character> stack;
    
    public Solution() {
        queue = new LinkedList();
        stack = new Stack();
    }
    
    public void pushCharacter(char ch) {
        stack.push(ch);
    }
    
    public void enqueueCharacter(char ch) {
        queue.add(ch);
    }
        
    public char popCharacter() {
        return stack.pop();
    }

    public char dequeueCharacter() {
        return queue.remove();
    }
}
