package com.example.appengine.java8;

public class Megabytes {


  public static String toString(double bytes) {
    return String.format("%.2f mb", bytes / 1024d / 1024d);
  }
}
