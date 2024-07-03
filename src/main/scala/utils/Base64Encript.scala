package utils


import java.util.Base64

object Base64Encript {

  def encode(content: Array[Byte]): String = {
    try {
      Base64.getEncoder().encodeToString(content)
    } catch {
      case e: Throwable => {
        println(e.getClass + ",  " + e.getMessage)
        ""
      }
    }
  }

  def decode(key: String): Array[Byte] = {
    try {
      Base64.getDecoder().decode(key.replaceAll("\n", "").replaceAll("\r", ""))
    } catch {
      case e: Throwable => {
        println(e.getClass + ",  " + e.getMessage)
        Array[Byte]()
      }
    }
  }
}

