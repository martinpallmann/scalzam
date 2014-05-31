package controllers

import play.api.mvc._
import java.io.{File, BufferedInputStream}
import com.sun.media.sound.WaveFileReader
import fft.{FastFourierTransformation, Complex}
import play.api.libs.json.JsArray
import play.api.libs.json.JsNumber

object Application extends Controller {

  val chunkSize = 4096

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def viewLastSpectrum = Action {
    Ok("OK")
  }

  def postAudio = Action(parse.multipartFormData) { request =>
    request.body.file("audio-blob").map { file =>
      val audio = readFile(file.ref.file)
      val transformed: Seq[Seq[Complex]] = toSeq(audio).map(FastFourierTransformation.fft)

      val buckets = List(0, 40, 80, 120, 180, Int.MaxValue).sliding(2).toList
      val result: Seq[List[Double]] = transformed.map(a => for (b <- buckets) yield Math.log(a.zipWithIndex.view(b.head, b.tail.head).toList.max._2.abs + 1))
      val hashes: Seq[Long] = result.map(hash)
      hashes.zipWithIndex.foreach{case (e, i) => println(i + " " + e)}
      Ok("OK")
    }.get
  }

  def toSeq(audio: List[Byte], chunkSize: Int = 4096): Seq[Seq[Complex]] = {
    audio.grouped(chunkSize).toSeq.map(_.map(a => Complex(a, 0))).takeWhile(_.size == chunkSize)
  }

  def hash(l: List[Double]): Long = {
    val damping = 2L
    l.foldLeft((0L, 100L)) {
      (acc, elem) => (acc._1 + (elem.toLong - elem.toLong % damping) * acc._2, acc._2 * 100L)
    }._1
  }

  private def readFile(file: File): List[Byte] = {
    val bis = new BufferedInputStream(new WaveFileReader().getAudioInputStream(file))
    Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toList
  }

  object Range {

    val max = 300
    val min = 0

    val ranges = Array(40, 80, 120, 180, 300)

    def range(index: Int): Int = index match {
      case i if i > 180 => 4
      case i if i > 120 => 3
      case i if i > 80 => 2
      case i if i > 40 => 1
      case _ => 0
    }
  }
}