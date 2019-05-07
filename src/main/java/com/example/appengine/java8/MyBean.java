package com.example.appengine.java8;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MyBean implements Serializable  {
  private String[] strings;


  public MyBean() {
    strings = new String[30_000];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = "hello" + ThreadLocalRandom.current().nextDouble();
    }
  }

  public String[] getStrings() {
    return strings;
  }
}
