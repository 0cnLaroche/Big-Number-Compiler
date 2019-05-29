package com.github.bignumbercompiler;

/**
 * Implémentation d'une pile
 * 
 * @author samuel
 */

public class Pile
{

  private String[] p;

  private int top;

  public Pile(int size)
  {
    p = new String[size];
    top = -1;
  }

  public void push(String x)
  {
    top++;
    p[top] = x;
  }

  public void push(char x)
  {
    top++;
    p[top] = String.valueOf(x);
  }

  public String pop() throws NegativeStackOverflowException
  {
    if (this.isVide())
    {
      throw new NegativeStackOverflowException();
    }
    else
    {
      top--;
    }
    return p[top + 1];
  }

  public String top()
  {
    return p[top];

  }

  public boolean isVide()
  {
    if (top == -1)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public String[] getArray()
  {
    return this.p;
  }

}
