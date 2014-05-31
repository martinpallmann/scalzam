package fft

import scala.math.{ cos, sin, cosh, sinh, pow, sqrt }

case class Complex(re: Double, im: Double) extends Ordered[Complex] {
  def +(x: Complex): Complex = Complex(re + x.re, im + x.im)
  def -(x: Complex): Complex = Complex(re - x.re, im - x.im)
  def *(x: Double):  Complex = Complex(re * x, im * x)
  def *(x: Complex): Complex = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
  def /(x: Double):  Complex = Complex(re / x, im / x)
  val abs = {

    sqrt(pow(re, 2.0) + pow(im, 2.0))
  }

  override def toString: String = {
    val a = "%1.3f" format re
    val b = "%1.3f" format Math.abs(im)
    (a,b) match {
      case (_, "0.000") => a
      case ("0.000", _) => b + "i"
      case (_, _) if im > 0 => a + " + " + b + "i"
      case (_, _) => a + " - " + b + "i"
    }
  }

  override def compare(that: Complex): Int = (this.abs * 100000 - that.abs * 100000).toInt
}

object Complex {
  def exp(c: Complex): Complex = {
    val r = cosh(c.re) + sinh(c.re)
    Complex(cos(c.im), sin(c.im)) * r
  }
}
