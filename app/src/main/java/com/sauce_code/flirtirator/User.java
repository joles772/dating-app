package com.sauce_code.flirtirator;

import java.util.ArrayList;

public class User implements Comparable {
    String id;
    String name;
    String age;
    String image;
    String sex;
    String description;
    ArrayList<String> matches;

    @Override
    public int compareTo(Object another) {
        return (this.id + "").compareTo(((User) another).id + "");
    }
}
