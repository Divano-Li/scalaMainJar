import httpClient.{HttpResponseAction, JsonMapper, RestClient}
import utils.{Md5Sign, TimeService}

object operator extends RestClient {
  def buildPartnerProductClientReq(phoneNum: String, num: Int, productFlag: String, cpOrderId: String, key: String, timeDelay: Int = 0): PartnerProductClientReq = {
    val time = TimeService.currentTime().delayDays(-timeDelay).currentTime
    val signTemp = s"id=$cpOrderId&num=$num&phoneNum=$phoneNum&productFlag=$productFlag&time=$time&key=$key"
    new PartnerProductClientReq(phoneNum, time, num, productFlag, cpOrderId, sign = Md5Sign.sign(signTemp))
  }

  def createPartnerPhoneOrder[A](partner: String, req: PartnerProductClientReq): HttpResponseAction = {
    val url = f"https://dev2-api.xunyou.mobi/api/v2/android/$partner/phones"
    postByUrl(url, JsonMapper.to[PartnerProductClientReq](req))
  }
}

class PartnerProductClientReq(val phoneNum: String,
                              val time: Long,
                              val num: Int,
                              val productFlag: String,
                              val id: String,
                              val salePrice: Int = 1200,
                              val sign: String)


