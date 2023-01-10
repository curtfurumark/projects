package se.curtrunebylund.projects.util;

import se.curtrunebylund.projects.infinity.ListItem;

public class Stack {
    private  int CAPACITY = 10;
    private int size = 0;
    private ListItem items[] = new ListItem[CAPACITY];
    public void push(ListItem item){
        Debug.log("Stack.push(ListItem)");
        if(size >= CAPACITY){
            //throw new Exception("max capacity");
            Debug.log("Stack.push reached max capacity...do something about it");
        }
        items[size++] = item;
    }
    public ListItem pop(){
        Debug.log("Stack.pop() size: " + size);
        size--;
        if( size <= 0){
            return null;
        }

        return items[size - 1];
    }

    public int getSize() {
        return size;
    }

    public ListItem get(int i) {
        return items[i];
    }
}
