package com.github.bignumbercompiler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;

public class BigDecimalCompiler
{

  private Pile registre;

  private HashMap<String, BigDecimal> memory;

  private static final int PRECISION = 9;

  //private static final Logger LOG = LoggerFactory.getLogger(BigDecimalCompiler.class);

  public BigDecimalCompiler()
  {
    this.registre = new Pile(10);
    this.memory = new HashMap<>(10);

    for (int i = 9; i >= 0; i--)
    {
      this.registre.push("R" + i);
    }
  }

  /**
   * Evaluate the priority of the operator. +, -, /, * and ^ accepted
   * 
   * @param item operator
   * @return integer representing the priority of operation
   */

  private int priority(String item)
  {

    int p = 0;

    switch (item)
    {

    case ")":
    case "(":
      p = 5;
      break;
    case "^":
      p = 4;
      break;
    case "*":
    case "/":
      p = 3;
      break;
    case "+":
    case "-":
      p = 2;
      break;
    case "=":
      p = 1;
      break;
    default:
      throw new IllegalArgumentException("Argument is not an operator");
    }

    return p;

  }

  /**
   * Converts an unfixed expression ( arithmetic ) to post-fixed expression
   * 
   * @param text source
   * @return array containing all members of the post-fixed expression
   */

  private String[] ConvPostfixe(String[] text)
  {

    Pile p = new Pile(text.length);
    Pile pf = new Pile(text.length); // Post-fixed pile

    try
    {

      for (int i = 0; i < text.length; i++)
      {

        String it = text[i];

        if (it.equals("("))
        {
          p.push(it);
        }
        else if (isOperator(it))
        {
          while (!p.isVide() && !p.top().equals("(") && (this.priority(it) <= priority(p.top())))
          {
            pf.push(p.top());
            p.pop();
          }
          p.push(it);
        }
        else if (it.equals(")"))
        {
          while (!p.top().equals("("))
          {
            pf.push(p.top());
            p.pop();
          }
          p.pop();
        }
        else // If it's a number (not an operator)
        {

          pf.push(it);
        }

      }
      while (!p.isVide())
      {
        pf.push(p.top());
        p.pop();
      }
    }
    catch (NegativeStackOverflowException e)
    {
      throw e;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      throw new ArithmeticException("Problem parsing expression " + Arrays.toString(text));
    }
    return pf.getArray();
  }

  /**
   * Evaluate a post-fixed expression and calculate the result in BigDecimal
   * 
   * @param ex Mathematical expression
   * @return value
   */

  private BigDecimal evaluatePostfixed(String[] ex)
  {

    Pile p = new Pile(ex.length);
    BigDecimal out = BigDecimal.valueOf(0);
    memory.put(registre.top(), out);

    try
    {
      for (int i = 0; i < ex.length; i++)
      {
        String it = ex[i];
        if (it == null || it.equals(" "))
        {
          // Ignore if null of empty string
        }
        else if (isOperand(it)) // If a number
        {
          p.push(it);
        }

        else if (isOperator(it))
        {

          String reg = new String();

          String arg2 = p.top();
          p.pop();

          String arg1 = p.top();
          p.pop();

          // Optimisation des régistres

          if (!isRegistre(arg1) && !isRegistre(arg2))
          {
            reg = registre.top();
            registre.pop();
          }
          else if (isRegistre(arg1) && isRegistre(arg2))
          {
            reg = arg1;
            registre.push(arg2);
          }
          else if (isRegistre(arg1))
          {
            reg = arg1;
          }
          else if (isRegistre(arg2))
          {
            reg = arg2;
          }

          String codeObjet = new String();

          // Imprimer le code objet dans la console

          switch (it)
          {
          case "+":
            memory.put(reg, getValue(arg1).add(getValue(arg2)));

            codeObjet = "ADD(\t" + reg + "," + arg1 + "," + arg2 + ")\n";
            break;
          case "-":
            memory.put(reg, getValue(arg1).subtract(getValue(arg2)));

            codeObjet = "SOU(\t" + reg + "," + arg1 + "," + arg2 + ")\n";
            break;
          case "*":
            memory.put(reg, getValue(arg1).multiply(getValue(arg2)));
            codeObjet = "MUL(\t" + reg + "," + arg1 + "," + arg2 + ")\n";
            break;
          case "/":
            memory.put(reg, getValue(arg1).divide(getValue(arg2), PRECISION, RoundingMode.HALF_UP));
            codeObjet = "DIV(\t" + reg + "," + arg1 + "," + arg2 + ")\n";
            break;
          case "^":
            memory.put(reg, getValue(arg1).pow(getValue(arg2).intValue()));
            codeObjet = "EXP(\t" + reg + "," + arg1 + "," + arg2 + ")\n";
            break;
          }
          //LOG.debug(codeObjet);
          System.out.print(codeObjet);
          p.push(reg);

        }
        else
        {
          throw new ArithmeticException(it + " is not an operator nor a operand");
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      throw new ArithmeticException("Problem parsing expression " + Arrays.toString(ex));
    }
    catch (NegativeStackOverflowException e)
    {
      throw e;
    }
    out = memory.get("R0");
    return out;
  }

  /**
   * Vefify if string is an operand. In mathematics an operand is the object of a mathematical
   * operation, . i.e. it is the object or quantity that is operated on
   * 
   * @param string to be verified
   * @return true if operand
   */

  private boolean isOperand(String string)
  {
    boolean decimal = false;

    for (int i = 0; i < string.length(); i++)
    {
      char c = string.charAt(i);
      if ((c >= '0' && c <= '9') || (c == '.' && !decimal))
      {
        if (c == '.')
        {
          decimal = true;
        }
        // keep looping
      }
      else
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Vefify if string is an operand. In mathematics an operand is the object of a mathematical
   * operation, . i.e. it is the object or quantity that is operated on
   * 
   * @param string to be verified
   * @return true if operand
   */

  private boolean isOperand(char c)
  {

    if ((c >= '0') && (c <= '9'))
    {
      return true;
    }
    else
    {
      return false;
    }

  }

  /**
   * Évalue si le caractère est un opérateur
   * 
   * @param it caractère
   * @return true si opérateur
   */
  private boolean isOperator(String it)
  {
    switch (it)
    {
    case "+":
      return true;
    case "-":
      return true;
    case "*":
      return true;
    case "/":
      return true;
    case "^":
      return true;
    case "=":
      return true;
    default:
      return false;
    }
  }

  /**
   * Évalue si la chaîne de caractère est une numéro de régistre
   * 
   * @param it chaîne de carctère
   * @return true si régistre
   */

  private boolean isRegistre(String it)
  {
    if (it.charAt(0) == 'R' && it.length() == 2 && isOperand(it.charAt(1)))
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  // ( ( 123 - 10.111 ) * ( 2.5 * 1 ) ) * ( 6 / 2 )

  /*{ "(", "(", "123", "-", "10.111", ")", "*", "(", "2.5", "*", "1", ")", ")", "*", "(", "6",
      "/", "2", ")" };*/

  public static BigDecimal evaluate(String[] source)
  {
    BigDecimalCompiler c = new BigDecimalCompiler();
    return c.evaluatePostfixed(c.ConvPostfixe(source));
  }

  public static BigDecimal evaluate(String source)
  {
    BigDecimalCompiler c = new BigDecimalCompiler();
    String[] parsed = source.split(" ");
    return c.evaluatePostfixed(c.ConvPostfixe(parsed));
  }

  public static BigDecimal evaluate(Object[] source)
  {
    BigDecimalCompiler c = new BigDecimalCompiler();
    String[] strArray = new String[source.length];
    for (int i = 0; i < source.length; i++)
    {
      if (source[i] instanceof BigDecimal)
      {
        BigDecimal bd = (BigDecimal) source[i];
        strArray[i] = bd.toPlainString();
      }
      else if (source[i] instanceof String)
      {
        String str = (String) source[i];
        strArray[i] = str;
      }
      else if (source[i] instanceof Integer)
      {
        int integer = (int) source[i];
        strArray[i] = Integer.toString(integer);
      }
      else if (source[i] instanceof Double)
      {
        double dbl = (double) source[i];
        strArray[i] = Double.toString(dbl);
      }
    }
    return c.evaluatePostfixed(c.ConvPostfixe(strArray));
  }

  /**
   * Evaluates if the value is a register, otherwise converts the String value to BigDecimal
   * <p>
   * 
   * @author Added May 27, 2019
   *         </p>
   * @param value
   * @return
   */

  private BigDecimal getValue(String value)
  {
    if (isRegistre(value))
    {
      return memory.get(value);
    }
    return new BigDecimal(value);
  }

}
