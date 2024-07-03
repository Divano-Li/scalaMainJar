package utils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object Gzip {
  def gzip(data: Array[Byte]): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val gzip = new GZIPOutputStream(bos)
    gzip.write(data)
    gzip.finish()
    gzip.close()
    val ret = bos.toByteArray
    bos.close()
    ret
  }
  def ungzip(data: Array[Byte]): Array[Byte] = {
    val size = 1024
    val bis = new ByteArrayInputStream(data)
    val gzip = new GZIPInputStream(bis)
    val buf = new Array[Byte](size)
    var num = -1
    val bos = new ByteArrayOutputStream
    val off = 0
    while ( {
      num = gzip.read(buf, off, buf.length)
      num != -1
    }) bos.write(buf, off, num)
    gzip.close()
    bis.close()
    val ret = bos.toByteArray
    bos.flush()
    bos.close()
    ret
  }
}
