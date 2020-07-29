package kr.co.tjoeun.colosseum_20200716.datas

import kr.co.tjoeun.colosseum_20200716.utils.TimeUtil
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Notification {

    var id = 0
    var title = ""

//    알림이 발생한 시간을 기록할 Calendar 변수
    val createAtCal = Calendar.getInstance()


    companion object {

//        simpleDateFormat은 고정양식 -> 한번만 만들고 재활용
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        fun getNotificationFromJson(json: JSONObject) : Notification {
            val s = Notification()

            s.id = json.getInt("id")
            s.title = json.getString("title")

//            발생한 시간 -> 양식을 맞춰서 string
            s.createAtCal.time = sdf.parse(json.getString("created_at"))

            val myPhoneTimeZone = s.createAtCal.timeZone

            val timeOffSet = myPhoneTimeZone.rawOffset / 1000 / 60 / 60

            s.createAtCal.add(Calendar.HOUR,timeOffSet)

            return s
        }

    }

}