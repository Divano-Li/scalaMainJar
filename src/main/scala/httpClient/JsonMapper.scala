package httpClient

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}
import utils.{Base64Encript, Gzip, XorEncrypt}

import scala.collection.JavaConverters._
import java.lang.reflect.{ParameterizedType, Type}

object JsonMapper {
  private val False = false

  private val Mapper: ObjectMapper =
    new ObjectMapper()
      .registerModule(DefaultScalaModule)
      .registerModule(new JodaModule())

  def to[T](value: T): String = {
    Mapper.writeValueAsString(value)
  }

  def from[T](value: String)(implicit m: Manifest[T]): T = {
    Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, False)
    Mapper.readValue[T](value, typeReference[T])
  }

  def toWithEncrypt[T](value: T): String = {
    val result = XorEncrypt.encrypt(Mapper.writeValueAsBytes(value), XorEncrypt.XorKey.getBytes("UTF-8"))
    Base64Encript.encode(result)
  }

  def fromWithEncrypt[T](value: String)(implicit m: Manifest[T]): T = {
    Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, False)
    val result = XorEncrypt.encrypt(Base64Encript.decode(value), XorEncrypt.XorKey.getBytes)
    Mapper.readValue[T](result, typeReference[T])
  }

  def toGzipEncrypt[T](value: T): String = {
    val temp = Mapper.writeValueAsBytes(value)
    val gzipTemp = Gzip.gzip(temp)
    val result = XorEncrypt.encrypt(gzipTemp, XorEncrypt.XorKey.getBytes("UTF-8"))
    Base64Encript.encode(result)
  }

  def fromGzipEncrypt[T](value: String)(implicit m: Manifest[T]): T = {
    Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, False)
    val temp = XorEncrypt.encrypt(Base64Encript.decode(value), XorEncrypt.XorKey.getBytes)
    val result = Gzip.ungzip(temp)
    Mapper.readValue[T](result, typeReference[T])
  }

  def fromGzipEncrypt1(value: String): String = {
    Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, False)
    val temp = XorEncrypt.encrypt(Base64Encript.decode(value), XorEncrypt.XorKey.getBytes)
    val result = Gzip.ungzip(temp)
    new String(result, "UTF-8")
  }

  private def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) {
      m.runtimeClass
    } else {
      new ParameterizedType {
        def getRawType = m.runtimeClass

        def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

        def getOwnerType = m.runtimeClass
      }
    }
  }
}

class ListDoubleDeserializer extends JsonDeserializer[List[Double]] {
  override def deserialize(jp: JsonParser, ctxt: DeserializationContext): List[Double] = {
    val javaList = jp.getCodec.readValue[java.util.List[java.lang.Number]](jp, ctxt.getTypeFactory.constructCollectionType(classOf[java.util.List[_]],
      classOf[java.lang.Number]))
    javaList.asScala.map(_.doubleValue).toList
  }
}

