package utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, TimeZone}
import scala.concurrent.duration
import scala.util.matching.Regex

// scalastyle:off named.argument

object TimeService {

  import duration._

  var UseFakeTime = false
  var UseSpecialInterval = false
  var FakeOffset: Long = 0
  val FakeTimeForUnitTest = 200
  val SecondsInOneHours = 3600
  val OneSecond = 1000
  val SpecialOneSecond: () => Int = () => if (UseFakeTime) 1 else OneSecond
  val LogDateFormat = "yyyy-MM-dd HH:mm:ss"
  val FullDateFormat = "yyyyMMddHHmmssSSS"
  val MonthPattern: Regex = """\d{4}-\d{2}""".r
  val DayPattern: Regex = """\d{4}-\d{2}-\d{2}""".r

  def sec: FiniteDuration = if (UseSpecialInterval) 1.milliseconds else 1.seconds

  def getAfterMonthFirstDay(month: Int): Date = {
    val calender = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    calender.setTime(currentTime().toDate)
    calender.set(Calendar.MONTH, calender.get(Calendar.MONTH) + month)
    calender.set(Calendar.DAY_OF_MONTH, 1)
    initializeDayTime(calender)
    calender.getTime
  }

  def getStartTimeOfDayAfterDays(days: Int): Date = {
    val calender = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    calender.setTime(currentTime().toDate)
    initializeDayTime(calender)
    calender.setTimeInMillis(calender.getTimeInMillis + 86400000 * days)
    calender.getTime
  }

  private def initializeDayTime(calendar: Calendar): Unit = {
    val beforeHalfDayInHours: Int = -12
    if (calendar.get(Calendar.AM_PM) == 0) {
      calendar.set(Calendar.HOUR, 0)
    } else {
      calendar.set(Calendar.HOUR, beforeHalfDayInHours)
    }
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
  }

  def getCurrentDate(): Int = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(TimeService.currentTime().toDate)
    cal.get(Calendar.DATE)
  }

  def getBaseDate: Date = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(TimeService.currentTime().toDate)
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    cal.set(year, month, 3, 0, 0, 0)
    cal.getTime
  }

  def currentTime(): Time = {
    new Time(System.currentTimeMillis + FakeOffset)
  }

  def customTime(milliSecond: Long): Time = {
    new Time(milliSecond)
  }

  def customTime(date: Date): Time = {
    new Time(date.getTime)
  }

  def getCurrentHour: Int = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(TimeService.currentTime().toDate)
    cal.get(Calendar.HOUR_OF_DAY)
  }

  def customTime(time: String, format: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): Time = {
    new Time(new SimpleDateFormat(format).parse(time).getTime)
  }

  def useFakeTime(milliSecond: Long = 0): Unit = {
    if (milliSecond != 0) {
      FakeOffset = milliSecond - System.currentTimeMillis
    }
    UseFakeTime = true
  }

  def useRealTime: Unit = {
    UseFakeTime = false
    FakeOffset = 0
  }

  def specialInterval(interval: Long): Long = {
    if (UseSpecialInterval) {
      FakeTimeForUnitTest
    } else {
      interval
    }
  }

  var UseFakeCheckWithinTime = false
  var CheckWithinTime: (Long, Long) => Boolean = (toCheck: Long, duration: Long) => {
    if (UseFakeCheckWithinTime) {
      false
    } else {
      System.currentTimeMillis() - toCheck < duration
    }
  }

  def createInitDate: Date = {
    val initTimeStamp: Int = 0
    new Date(initTimeStamp)
  }


  def isToday(date: Date): Boolean = {
    customTime(date).toString("yyyy-MM-dd") == currentTime().toString("yyyy-MM-dd")
  }
}

object Time {
  def apply(timestamp: Long): Time = {
    new Time(timestamp)
  }
}

class Time(var currentTime: Long) {
  var dateTime: String = setFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").toString()

  def setFormat(format: String, zone: String = "GMT+8"): Time = {
    dateTime = formatConvert(new Date(currentTime), format, zone)
    this
  }

  def delayMillis(millis: Int): Time = {
    if (TimeService.UseFakeTime) {
      currentTime = currentTime + millis
    } else {
      currentTime = currentTime + millis.toLong
    }
    this
  }

  def delayMillis(millis: Long): Time = {
    currentTime = currentTime + millis
    this
  }

  def delaySeconds(second: Int): Time = {
    if (TimeService.UseFakeTime) {
      currentTime = currentTime + second
    } else {
      currentTime = currentTime + second.toLong * 1000
    }
    this
  }

  def delayMinutes(minute: Int): Time = {
    if (TimeService.UseFakeTime) {
      currentTime = currentTime + minute
    } else {
      currentTime = currentTime + minute.toLong * 60 * 1000
    }
    this
  }

  def delayHours(hour: Int): Time = {
    if (TimeService.UseFakeTime) {
      currentTime = currentTime + hour
    } else {
      currentTime = currentTime + hour.toLong * 60 * 60 * 1000
    }
    this
  }

  def delayDays(day: Int): Time = {
    currentTime = currentTime + day.toLong * 24 * 60 * 60 * 1000
    this
  }

  def delayYears(year: Int): Time = {
    if (TimeService.UseFakeTime) {
      currentTime = currentTime + year
    } else {
      val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
      cal.set(this.toYear + year, this.toMonth, this.toDayOfMonth, 0, 0, 0)
      currentTime = cal.getTimeInMillis
    }
    this
  }

  def delayMonth(month: Int): Time = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(currentTime))
    cal.add(Calendar.MONTH, month)
    currentTime = cal.getTime.getTime
    this
  }

  def beforeMonth(month: Int): Time = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(currentTime))
    cal.add(Calendar.MONTH, -month)
    currentTime = cal.getTime.getTime
    this
  }

  def toDayBegin(): Long = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(currentTime))
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0)
    cal.getTimeInMillis
  }

  // 一周的周一为开始，但calendar是以周日为一周的开始
  def toWeekBegin(): Long = {
    val offset = 6
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(currentTime))
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0)
    val interval = if (cal.get(Calendar.DAY_OF_WEEK) == 1) offset else cal.get(Calendar.DAY_OF_WEEK) - 2
    cal.getTimeInMillis - interval * 86400 * 1000
  }

  override def toString: String = {
    dateTime
  }

  def toString(dateFmt: String): String = {
    val df: SimpleDateFormat = new SimpleDateFormat(dateFmt)
    df.setTimeZone(TimeZone.getTimeZone("GMT+8"))
    df.format(new Date(currentTime))
  }

  def rounding(dateFmt: String): Time = {
    TimeService.customTime(toString(dateFmt), dateFmt)
  }

  def toDate: Date = {
    new Date(currentTime)
  }

  def toDate0Clock: Date = {
    val cal: Calendar = Calendar.getInstance()
    cal.setTime(new Date(currentTime))
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  def toMilliSecond: Long = {
    currentTime
  }

  def toSecond: Long = {
    currentTime / 1000
  }

  def toDayOfMonth: Int = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(currentTime))
    cal.get(Calendar.DAY_OF_MONTH)
  }

  def toMonth: Int = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(currentTime))
    cal.get(Calendar.MONTH)
  }

  def toYear: Int = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(currentTime))
    cal.get(Calendar.YEAR)
  }

  def toWeek(minDay: Int = 1): Int = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setMinimalDaysInFirstWeek(minDay)
    cal.setTime(new Date(currentTime))
    cal.get(Calendar.WEEK_OF_YEAR)
  }

  def toDayOfWeek: String = {
    val dayOfWeek = Map(1 -> "Sun", 2 -> "Mon", 3 -> "Tus", 4 -> "Wed", 5 -> "Thu", 6 -> "Fri", 7 -> "Sat")
    dayOfWeek(getDayOfWeek(currentTime))
  }

  def toDayNumOfWeek: Int = {
    val dayOfWeek = Map(1 -> 7, 2 -> 1, 3 -> 2, 4 -> 3, 5 -> 4, 6 -> 5, 7 -> 6)
    dayOfWeek(getDayOfWeek(currentTime))
  }

  private def formatConvert(date: Date, format: String, zone: String): String = {
    val df: SimpleDateFormat = new SimpleDateFormat(format)
    if (zone != "") {
      df.setTimeZone(TimeZone.getTimeZone(zone))
    }
    df.format(date)
  }

  private def getDayOfWeek(time: Long): Int = {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    cal.setTime(new Date(time))
    cal.get(Calendar.DAY_OF_WEEK)
  }
}

// scalastyle:on named.argument

