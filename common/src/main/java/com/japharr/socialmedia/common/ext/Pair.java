package com.japharr.socialmedia.common.ext;

public class Pair<X, Y> {
  private X x;
  private Y y;

  private Pair(X x, Y y) {
    this.x = x;
    this.y = y;
  }

  public static <X, Y> Pair<X, Y> of(X x, Y y) {
    return new Pair<>(x, y);
  }

  public X x() {
    return x;
  }

  public Y y() {
    return y;
  }
}
