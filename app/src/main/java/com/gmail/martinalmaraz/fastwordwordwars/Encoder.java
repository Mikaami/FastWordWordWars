package com.gmail.martinalmaraz.fastwordwordwars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by kami on 5/5/2016.
 */
public class Encoder
{
    public int[] arr;

    public Encoder(File file) throws IOException
    {
        String word;
        char[] holder;
        arr = new int[26];
        Scanner in = new Scanner(file);
        while(in.hasNext())
        {
            word = in.nextLine();
            holder = word.toCharArray();

            for(int i = 0; i < holder.length; i++)
            {
                arr[((int) holder[i] % 97)]++;
            }
        }
        createTree();
    }

    public void createTree()
    {
        ArrayList<Node> list = new ArrayList<>();
        char x;
        String y;
        for(int i = 0; i < arr.length; i++)
        {
            x = (char)(97 + i);
            list.add(new Node(null, null, arr[i], String.valueOf(x)));
        }

    }


    public class Node
    {
        public Node leftPtr;
        public Node rightPtr;
        public double weight;
        public String value;

        public Node(Node left, Node right, double weight, String val)
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

        public double getWeight()
        {
            return this.weight;
        }
        public String getValue()
        {
            return  this.value;
        }
    }


}
