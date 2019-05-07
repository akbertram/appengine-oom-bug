package com.example.appengine.java8;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MyBean implements Serializable  {
  private Double x;
  private String foo;
  private Set<Integer> set;


  public MyBean() {
    x = ThreadLocalRandom.current().nextDouble();
    foo = "Hello hello world " + ThreadLocalRandom.current().nextInt();
    set = new HashSet<>();
    set.add(ThreadLocalRandom.current().nextInt());
    set.add(ThreadLocalRandom.current().nextInt());
    set.add(ThreadLocalRandom.current().nextInt());
    set.add(ThreadLocalRandom.current().nextInt());
    set.add(ThreadLocalRandom.current().nextInt());
  }

  double getTotal() {
    return x + foo.length() + set.size();
  }

  @Override
  public String toString() {
    return "MyBean{" +
        "x=" + x +
        ", foo='" + foo + '\'' +
        ", set=" + set +
        '}';
  }
}
