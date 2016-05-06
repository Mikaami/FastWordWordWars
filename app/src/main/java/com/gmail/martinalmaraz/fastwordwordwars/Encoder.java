package com.gmail.martinalmaraz.fastwordwordwars;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Created by kami on 5/5/2016.
 */
public class Encoder
{
    public int[] arr;
    public Node root;

    public Encoder(InputStream file) throws IOException
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
                if(((int) holder[i]) % 97 <= 25)
                {
                    arr[(((int) holder[i]) % 97)]++;
                }
            }
        }
        createTree();
    }

    public void createTree()
    {
        ArrayList<Node> list = new ArrayList<>();
        char x;
        for(int i = 0; i < arr.length; i++)
        {
            x = (char)(97 + i);
            Log.d("encoder", "value i = " + x);
            list.add(new Node(null, null, arr[i], String.valueOf(x)));
        }
        while(list.size() > 1)
        {
            Collections.sort(list, new CustomComparator());
            Log.d("encoder", "List 0: " + list.get(0).getWeight());
            Log.d("encoder", "List 1: " + list.get(1).getWeight());
            list.add(new Node(list.get(0), list.get(1), (list.get(0).getWeight() + list.get(1).getWeight())));
            list.remove(1);
            list.remove(0);
        }
        root = list.get(0);
    }

    public void decode()
    {

    }

    public class CustomComparator implements Comparator<Node>
    {
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
        public double weight;
        public String value;

        public Node(Node left, Node right, double weight, String val)
        {
            this.leftPtr = left;
            this.rightPtr = right;
            this.weight = weight;
            this.value = val;
        }

        public Node(Node left, Node right, double weight)
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

        //compareTo will return true if it is greater than the obj it is being compared to
        public int compareTo(Node obj)
        {
            return Double.compare(this.getWeight(), obj.getWeight());
        }
    }




}
