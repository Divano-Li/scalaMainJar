package utils

import java.security.MessageDigest
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import javax.xml.bind.DatatypeConverter
import scala.collection.mutable.ArrayBuffer

object XorEncrypt {
  val XorKey = "qIudQA46JWmRAB8gp3MNBVuGY9weiCLEz5g2iaUFmEH7NThORI7yHStMLuJ7h1AQwlzSrU9AlzpsPm9PfLtvvpVd95ase7YniGyF5yLa7jpqfhjpeNwFQyUpUXXs9UnL"

  def encrypt(data: Array[Byte], key: Array[Byte]): Array[Byte] = {
    val result = new Array[Byte](data.length)
    for {i <- data.indices} {
      result(i) = (data(i) ^ key(i % key.length)).toByte
    }
    result
  }

  val BlackNetSegmentsXorKey = "Xunyou"

  def simpleDecryptionIndexedXOR(encryptedData: Array[Byte], salt: String): Array[Byte] = {
    val md5Hash = MessageDigest.getInstance("MD5").digest(salt.getBytes(StandardCharsets.UTF_8))
    val signum = 1
    val md5Hash2BigInt  = new BigInteger(signum, md5Hash)
    val md5Str = String.format("%032x", md5Hash2BigInt)
    val keySize = md5Str.length() * 2
    val utf16Runes = md5Str.toCharArray.map(_.toShort)
    val pkBuffer = utf16Runes.flatMap(r => Array((r & 0xFF).toByte, (r >> 8).toByte))

    val dataLength = encryptedData.length
    if (dataLength == 0) {
      Array[Byte]()
    } else {
      val resultBuffer = new Array[Byte](dataLength)
      val firstIndex = 0
      var lastByte = encryptedData(firstIndex)
      resultBuffer(firstIndex) = lastByte

      for {i <- 1 until dataLength} {
        val cByte = encryptedData(i)
        val bByte = (cByte ^ pkBuffer(i % keySize)).toByte
        val aByte = (bByte ^ lastByte).toByte
        val byte1 = (aByte - i).toByte
        resultBuffer(i) = byte1
        lastByte = byte1
      }

      resultBuffer
    }
  }

  def simpleEncryptionIndexedAndXOR(sourceData: Array[Byte], salt: String): Array[Byte] = {
    val md5Hash: Array[Byte] = MessageDigest.getInstance("MD5").digest(salt.getBytes("UTF-8"))
    val md5Str: String = DatatypeConverter.printHexBinary(md5Hash).toLowerCase
    val keySize: Int = md5Str.length * 2

    //  将字符串转换为UTF-16编码的字节切片
    val utf16Runes: Array[Char] = md5Str.toCharArray
    val pkBuffer: ArrayBuffer[Byte] = ArrayBuffer[Byte]()
    for {r <- utf16Runes} {
      pkBuffer += (r & 0xFF).toByte
      pkBuffer += (r >> 8).toByte
    }

    val dataLength: Int = sourceData.length
    if (dataLength == 0) {
      Array[Byte]()
    } else {
      val resultBuffer: ArrayBuffer[Byte] = ArrayBuffer[Byte]()
      val firstIndex = 0
      resultBuffer += sourceData(firstIndex)
      var lastByte: Byte = sourceData(firstIndex)

      for {i <- 1 until dataLength} {
        val byte1: Byte = sourceData(i)
        val aByte: Byte = (byte1 + i).toByte
        val bByte: Byte = (aByte ^ lastByte).toByte
        val cByte: Byte = (bByte ^ pkBuffer(i % keySize)).toByte
        resultBuffer += cByte
        lastByte = byte1
      }

      resultBuffer.toArray
    }
  }
}

