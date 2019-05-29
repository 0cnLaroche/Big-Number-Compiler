package com.github.bignumbercompiler;

import static org.junit.Assert.*;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalCompilerTest
{
  private static final String VALID_EXPRESSION1 = "( 12315 - 10.111 ) * ( 15.23 / 4 )";

  private static final String VALID_EXPRESSION2 =
      "( 123 * 10.11145 ) / ( 15.238443 * ( 11.123 - 4 ) )";

  private static final String VALID_EXPRESSION3 = "123152.1555551484 + 10248.15488466111";

  private static final String INVALID_EXPRESSION1 = "12315 - 10.111 ) * ( 15.23 / 4 )";

  private static final String INVALID_EXPRESSION2 = "12315 * * 10.111";

  private static final String INVALID_EXPRESSION3 = "12315 / 0";

  private static final int PRECISION = 9;

  @Test
  public void testCompileFromString()
  {
    BigDecimal result1 = BigDecimalCompiler.evaluate(VALID_EXPRESSION1);

    BigDecimal expected1 = BigDecimal.valueOf(12315).subtract(BigDecimal.valueOf(10.111)).multiply(
        BigDecimal.valueOf(15.23).divide(BigDecimal.valueOf(4), PRECISION, RoundingMode.HALF_UP));

    System.out.println("Expected from " + VALID_EXPRESSION1 + " = " + expected1);

    assertEquals(expected1, result1);

    BigDecimal result2 = BigDecimalCompiler.evaluate(VALID_EXPRESSION2);

    BigDecimal expected2 = BigDecimal.valueOf(123).multiply(BigDecimal.valueOf(10.11145)).divide(
        BigDecimal.valueOf(15.238443).multiply(BigDecimal.valueOf(11.123).subtract(BigDecimal
            .valueOf(4))), PRECISION, RoundingMode.HALF_UP);

    System.out.println("Expected from " + VALID_EXPRESSION2 + " = " + expected2);

    assertEquals(expected2, result2);

    BigDecimal result3 = BigDecimalCompiler.evaluate(VALID_EXPRESSION3);

    BigDecimal expected3 = BigDecimal.valueOf(123152.1555551484).add(BigDecimal.valueOf(
        10248.15488466111));

    System.out.println("Expected from " + VALID_EXPRESSION3 + " = " + expected3);

    assertEquals(expected3, result3);

  }

  @Test(expected = ArithmeticException.class)
  public void testDivideByZero()
  {
    BigDecimalCompiler.evaluate(INVALID_EXPRESSION3);
  }

  @Test(expected = ArithmeticException.class)
  public void testMissingParenthese()
  {
    BigDecimalCompiler.evaluate(INVALID_EXPRESSION1);
  }

  @Test(expected = ArithmeticException.class)
  public void testBadExpression1()
  {
    BigDecimalCompiler.evaluate(INVALID_EXPRESSION2);
  }

}
