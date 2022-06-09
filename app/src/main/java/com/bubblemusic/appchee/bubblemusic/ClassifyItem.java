package com.bubblemusic.appchee.bubblemusic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ClassifyItem implements Serializable {
    String key=null;
    ArrayList<MusicItem> item=new ArrayList<>();;

    public ClassifyItem(String key)
    {
        //this.item=new ArrayList<>();
        this.key=key;
    }

    public int getCount()
    {
        return item.size();
    }

    public void setKey(String k) {
        this.key=k;
    }

    public String getKey() {
        return this.key;
    }

    public ArrayList<MusicItem> getItem() {
        return item;
    }

    public void addItem(MusicItem i) {
        this.item.add(i);
    }

    public void addallItem(ArrayList<MusicItem> all) {
        item.addAll(all);
    }

    public void itemSort()
    {
        Collections.sort(item,titlesort);
    }
    public static Comparator<MusicItem> titlesort =new Comparator<MusicItem>() {
        @Override
        public int compare(MusicItem o1, MusicItem o2) {
            return o1.getTitle().compareToIgnoreCase(o2.getTitle());
        }
    };
}
