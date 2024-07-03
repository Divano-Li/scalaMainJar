package utils

import java.security.MessageDigest
import scala.collection.mutable

object Md5Sign {
  def sign(value: String): String = {
    val capacitySize = 32
    val endIndex = 15
    try {
      val md5handle = MessageDigest.getInstance("MD5")
      val hexDigits = List[String]("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f").flatMap(o => o.toCharArray).toArray
      val encrypt = md5handle.digest(value.getBytes("utf-8"))
      val b = new StringBuilder(capacity = capacitySize)
      for (i <- 0.to(end = endIndex)) {
        b.append(hexDigits(encrypt(i) >>> 4 & 0xf)).append(hexDigits(encrypt(i) & 0xf))
      }
      b.mkString.toUpperCase()
    } catch {
      case e: Throwable => {
        println("Md5 Sign error: " + e.getMessage)
        ""
      }
    }
  }
  def verify(paras: mutable.Map[String, String], key: String): Boolean = {
    val preSignStr = createLink(paraFilter(paras))
    val signValue = sign(preSignStr + "&key=" + key)
    signValue.equals(paras.get("sign").getOrElse("no sign"))
  }

  def paraFilter(sArray: mutable.Map[String, String], name: String = "sign"): mutable.Map[String, String] = {
    if ((sArray == null) || (sArray.size <= 0)) {
      mutable.Map[String, String]()
    } else {
      sArray.filter(_._2 != null)
        .filter(_._2 != "")
        .filter(!_._1.equalsIgnoreCase(name))
        .filter(!_._1.equalsIgnoreCase("sign_type"))
    }
  }

  def createLink(params: mutable.Map[String, String]): String = {
    var prestr = ""
    params.keySet.toList.sortBy(key => key).foreach(key => {
      prestr = prestr + key + "=" + params(key) + "&"
    })
    prestr.dropRight(n = 1)
  }
}

