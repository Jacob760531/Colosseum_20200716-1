package kr.co.tjoeun.colosseum_20200716

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_notification_list.*
import kotlinx.android.synthetic.main.activity_view_reply_detail.*
import kotlinx.android.synthetic.main.notification_list_item.*
import kr.co.tjoeun.colosseum_20200716.adapters.NotificationAdapter

import kr.co.tjoeun.colosseum_20200716.datas.Notification
import kr.co.tjoeun.colosseum_20200716.datas.Reply
import kr.co.tjoeun.colosseum_20200716.datas.Reply.Companion.getReplyFromJson
import kr.co.tjoeun.colosseum_20200716.utils.ServerUtil
import kr.co.tjoeun.colosseum_20200716.utils.TimeUtil
import org.json.JSONObject

class NotificationListActivity : BaseActivity() {

    val mNotiList = ArrayList<Notification>()
    lateinit var mNotiAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)

        setupEvents()
        setValues()
    }


    override fun setupEvents() {
    }

    override fun setValues() {
        getNotiListFromServer()

        mNotiAdapter = NotificationAdapter(mContext,R.layout.notification_list_item,mNotiList)
        notiListView.adapter = mNotiAdapter

    }

//    서버에서 의견 정보 불러오기

    fun getNotiListFromServer() {

        ServerUtil.getRequestNotificationList(mContext,object : ServerUtil.JsonResponseHandler {
            override fun onResponse(json: JSONObject) {

                val data = json.getJSONObject("data")
                val notifications = data.getJSONArray("notifications")

                for (i in 0 until notifications.length()) {
                    mNotiList.add(Notification.getNotificationFromJson(notifications.getJSONObject(i)))
                }

//                알림이 하나라도 있다면 => 알림을 어디까지 읽었는지 서버에 전송해주자.
//                그래야 메인화면에서 알림 갯수를 0개로 만들 수 있다.
//                알림을 어디까지 읽었는지 알려주고서는 -> 아무일도 하지 않을 예정
//                handler에 null을 넣어서, 할일이 없다고 명시

                ServerUtil.postRequestNotificationCheck(mContext,mNotiList[0].id, null)

//                mReply 내부의 변수들을 => 화면에 반영

                runOnUiThread {

//                    댓글 목록을 불러왔다고 리스트뷰 어댑터에게 알림
                    mNotiAdapter.notifyDataSetChanged()
//                    NotificationAdapter.notifyDataSetChanged()


                }
            }
        })
    }

}