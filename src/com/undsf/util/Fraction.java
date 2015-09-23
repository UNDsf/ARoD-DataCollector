package com.undsf.util;

import java.math.BigInteger;

public class Fraction {
    protected BigInteger numerator, denominator, gcd;

    public void set(String n, String d) {
        numerator = new BigInteger(n);
        denominator = new BigInteger(d);
    }

    public void simplify() {
        gcd = numerator.gcd(denominator);
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);
        if (denominator.compareTo(BigInteger.ZERO) < 0) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }
    }

    public int compareTo(Fraction y) {
        Fraction z = this.subtract(y);
        return z.numerator.compareTo(BigInteger.ZERO);
    }

    public Fraction abs() {
        Fraction z = new Fraction();
        if (numerator.compareTo(BigInteger.ZERO) < 0) {
            z.numerator = numerator.negate();
        } else {
            z.numerator = numerator;
        }
        if (denominator.compareTo(BigInteger.ZERO) < 0) {
            z.denominator = denominator.negate();
        } else {
            z.denominator = denominator;
        }
        return z;
    }

    public Fraction negate() {
        Fraction z = this;
        z.numerator = z.numerator.negate();
        return z;
    }

    public boolean zero() {
        return numerator.compareTo(BigInteger.ZERO) == 0;
    }

    public Fraction add(Fraction y) {
        Fraction z = new Fraction();
        gcd = denominator.gcd(y.denominator);
        z.denominator = denominator.multiply(y.denominator).divide(gcd);
        z.numerator = numerator.multiply(z.denominator.divide(denominator)).add(y.numerator.multiply(z.denominator.divide(y.denominator)));
        z.simplify();
        return z;
    }

    public Fraction subtract(Fraction y) {
        Fraction z = new Fraction();
        gcd = denominator.gcd(y.denominator);
        z.denominator = denominator.multiply(y.denominator).divide(gcd);
        z.numerator = numerator.multiply(z.denominator.divide(denominator)).subtract(y.numerator.multiply(z.denominator.divide(y.denominator)));
        z.simplify();
        return z;
    }

    public Fraction multiply(Fraction y) {
        Fraction z = new Fraction();
        z.numerator = numerator.multiply(y.numerator);
        z.denominator = denominator.multiply(y.denominator);
        z.simplify();
        return z;
    }

    public Fraction divide(Fraction y) {
        Fraction z = new Fraction();
        z.numerator = numerator.multiply(y.denominator);
        z.denominator = denominator.multiply(y.numerator);
        z.simplify();
        return z;
    }

    @Override
    public String toString(){
    	if (denominator.compareTo(BigInteger.ONE) != 0) {
            return numerator.toString()+"/"+denominator.toString();
        } else {
            return numerator.toString();
        }
    }
}