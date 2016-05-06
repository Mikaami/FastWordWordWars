package com.gmail.martinalmaraz.fastwordwordwars;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by kami on 5/5/2016.
 */
public class Encoder
{
    public int[] freq;
    public Node root;
    public HashMap<String, String> keys = new HashMap<>();

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
        buildKeySet(root, "");
        printTree();
    }

    public void printTree()
    {
        Iterator it = keys.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            Log.d("map", "key: " + pair.getKey() + ", value: " + pair.getValue());
            //it.remove();
        }
    }


    public void buildKeySet(Node node, String s)
    {
        if(node == null)
            return;
        if(node.isleaf())
        {
            if(node.getValue() != null)
                keys.put(node.getValue(), s);
            return;
        }
        buildKeySet(node.leftPtr,  s + "0");
        buildKeySet(node.rightPtr, s + "1");
    }

    public void createTree()
    {
        ArrayList<Node> list = new ArrayList<>();
        char x;
        for(int i = 0; i < freq.length; i++)
        {
            //creating nodes based on their weight and values
            x = (char)(97 + i);
            Log.d("encoder", "value i = " + x + " " + freq[i]);
            list.add(new Node(null, null, freq[i], String.valueOf(x)));
        }
        while(list.size() > 1)
        {
            //sorts the list to account for new nodes
            Collections.sort(list, new CustomComparator());
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
        for(boolean b : bit)
            Log.d("fucked", String.valueOf(b));
        for(int i = 0; i < bit.length; i++)
        {
            Log.d("decode", "valueCurr: " + curr.getValue());
            if(curr.isleaf())
            {
                Log.d("decode", "was leaf -> " + curr.getValue());
                word += curr.getValue();
                curr = root;
                //i--;
            }
            else if(bit[i])
            {
                Log.d("decode", "went right");
                curr = curr.rightPtr;
                if(curr.isleaf())
                {
                    Log.d("decode", "was leaf -> " + curr.getValue());
                    word += curr.getValue();
                    curr = root;
                    //i--;
                }
            }
            else
            {
                Log.d("decode", "went left");
                curr = curr.leftPtr;
                if(curr.isleaf())
                {
                    Log.d("decode", "was leaf -> " + curr.getValue());
                    word += curr.getValue();
                    curr = root;
                    //i--;
                }
            }

        }
        return word;
    }

    public byte[] toBytes(boolean[] input)
    {
        byte[] toReturn = new byte[input.length / 8];
        for(int entry = 0; entry < toReturn.length; entry++)
        {
            for(int bit = 0; bit < 8; bit++)
            {
                if(input[entry * 8 + bit])
                {
                    toReturn[entry] |= (128>> bit);
                }
            }
        }
        return toReturn;
    }

    public boolean[] toBoolean(byte[] input)
    {
        boolean[] toReturn = new boolean[input.length * 8];

        int j = 0;
        for(byte b : input)
        {
            toReturn[j++] = ((b & 0x01) != 0);
            toReturn[j++] = ((b & 0x02) != 0);
            toReturn[j++] = ((b & 0x04) != 0);
            toReturn[j++] = ((b & 0x08) != 0);

            toReturn[j++] = ((b & (0x10)) != 0);
            toReturn[j++] = ((b & (0x20)) != 0);
            toReturn[j++] = ((b & (0x40)) != 0);
            toReturn[j++] = ((b & (0x80)) != 0);

        }
        return toReturn;
    }

    public  boolean[] encode(String word)
    {
        char[] alpha = word.toCharArray();
        Log.d("encoder", "before height");
        boolean[] bits = new boolean[root.getHeight()*word.length()];
        int x = 0;

        for(char c : alpha)
        {
            String values = keys.get(String.valueOf(c));
            Log.d("convert", String.valueOf(c));
            Log.d("convert", Integer.toString(keys.size()));
            if(values == null)
                Log.d("convert", "was null");
            for(char v : values.toCharArray())
            {
                if(v == '1')
                    bits[x++] = true;
                else if(v == '0')
                    bits[x++] = false;
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
