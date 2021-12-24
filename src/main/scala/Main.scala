import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._

import net.liftweb.json._
import net.liftweb.json.JsonDSL._

import java.io.{BufferedWriter, File, FileWriter}
import scala.collection.mutable.ListBuffer


object Main {

  def main(args: Array[String]) = {

    val page = JsoupBrowser().get("https://dic.academic.ru/dic.nsf/ruwiki/1638673")
    val info = ListBuffer.empty ++= (page >> elementList("table.toccolours.sortable tr") >> elementList("td").map(_ >> allText("td")))

    info.remove(0)
    info.remove(info.size - 1)

    for (i <- info) {
      val json =
        ("name" -> i.head) ~
          ("mass" -> clearData(i(1))) ~
          ("radius" -> clearData(i(2))) ~
          ("orbPeriod" -> clearData(i(3))) ~
          ("semiMajorAxis" -> clearData(i(4))) ~
          ("orbitalEccentricity" -> clearData(i(5))) ~
          ("incline" -> clearData(i(6))) ~
          ("year" ->i(7).toInt)

      writeFile(compactRender(json), info.indexOf(i).toString)
    }
  }

  def clearData(str: String): Option[Double] = str match{
    case "-" => None
    case _ => Some(str.replaceAll("[~|-|<|>|\\s|?]", "").replace(',', '.').toDouble)
  }

  def writeFile(json: String, name: String): Unit = {
    val dir = new File("C:\\JsonFiles\\").mkdir()
    val file = new File("C:\\JsonFiles\\" + s"${name}" + ".json")
    val buffer = new BufferedWriter(new FileWriter(file))
    buffer.write(json)
    buffer.close()
  }

}
