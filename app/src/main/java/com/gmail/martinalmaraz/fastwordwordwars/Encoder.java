package com.gmail.martinalmaraz.fastwordwordwars;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by kami on 5/5/2016.
 */
public class Encoder
{
    public int[] freq;
    public Node root;

    public Encoder(InputStream file) throws IOException
    {
        //Takes in a file of words to create a huffman tree
        /*It will count the characters in the file and use that to
        * determine the weight of the tree. */
        String word;
        char[] holder;
        //It holds each character based on its position in the alphabet
        freq = new int[26];
        Scanner in = new Scanner(file);
        while(in.hasNext())
        {
            word = in.nextLine();
            holder = word.toCharArray();

            for(int i = 0; i < holder.length; i++)
            {
                if(((int) holder[i]) % 97 <= 25)
                {
                    freq[(((int) holder[i]) % 97)]++;    //Converts char to position in arr
                }
            }
        }
        createTree();
    }

    public void createTree()
    {
        ArrayList<Node> list = new ArrayList<>();
        char x;
        for(int i = 0; i < freq.length; i++)
        {
            //creating nodes based on their weight and values
            x = (char)(97 + i);
            Log.d("encoder", "value i = " + x);
            list.add(new Node(null, null, freq[i], String.valueOf(x)));
        }
        while(list.size() > 1)
        {
            //sorts the list to account for new nodes
            Collections.sort(list, new CustomComparator());
            Log.d("encoder", "List 0: " + list.get(0).getWeight());
            Log.d("encoder", "List 1: " + list.get(1).getWeight());
            //Combines the two smallest nodes to create a new node with a new weight.
            list.add(new Node(list.get(0), list.get(1), (list.get(0).getWeight() + list.get(1).getWeight())));
            //Removes the first two nodes it used to create the new node.
            list.remove(1);
            list.remove(0);
        }
        //Sets the root ptr to the only node left
        root = list.get(0);
    }

    public String decode(boolean[] bit)
    {
        Node curr = root;
        String word = "";
        for(int i = 0; i < bit.length; i++)
        {

            if(curr.isleaf())
            {
                word += curr.getValue();
                curr = root;
                i--;
            }
            else if(bit[i])
            {
                curr = curr.rightPtr;
            }
            else
                curr = curr.leftPtr;
        }
        return word;
    }

    public  boolean[] encode(String word)
    {
        char[] alpha = word.toCharArray();
        boolean[] bits = new boolean[root.getHeight()*word.length()];
        Node curr = root;
        int x = 0;
        boolean flag;

        for(int i = 0; i < alpha.length; i ++)
        {
            flag = true;
            while(flag)
            {
                if (freq[((int) alpha[i]) % 97] == curr.getWeight() && curr.getValue().equals(String.valueOf(alpha[i])))
                {
                    curr = root;
                    flag = false;
                }
                else if (freq[((int) alpha[i]) % 97] > curr.getWeight())
                {
                    curr = curr.rightPtr;
                    bits[x] = true;
                    x++;
                }
                else
                {
                    curr = curr.leftPtr;
                    bits[x] = false;
                    x++;
                }
            }
        }

        return Arrays.copyOfRange(bits, 0, x);
    }

    public class CustomComparator implements Comparator<Node>
    {
        //new class to handle sorting of a custom class
        @Override
        public  int compare(Node obj1, Node obj2)
        {
            return  obj1.compareTo(obj2);
        }
    }


    public class Node
    {
        public Node leftPtr;
        public Node rightPtr;
        public int weight;
        public String value;

        public Node(Node left, Node right, int weight, String val)
        {
            this.leftPtr = left;
            this.rightPtr = right;
            this.weight = weight;
            this.value = val;
        }

        public Node(Node left, Node right, int weight)
        {
            this(left, right, weight, null);
        }

        public boolean isleaf()
        {
            return (leftPtr == null) && (rightPtr == null);
        }

        public int getWeight()
        {
            return this.weight;
        }
        public String getValue()
        {
            return  this.value;
        }

        //added to compare two nodes to sort
        public int compareTo(Node obj)
        {
            return Double.compare(this.getWeight(), obj.getWeight());
        }
        public int getHeight()
        {
            if(this.isleaf())
                return 1;
            else
                return 1 + Math.max(this.rightPtr.getHeight(), this.leftPtr.getHeight());


        }
    }




}
