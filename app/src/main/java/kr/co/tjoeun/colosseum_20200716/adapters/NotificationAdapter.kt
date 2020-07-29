package kr.co.tjoeun.colosseum_20200716.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_reply_detail.*
import kr.co.tjoeun.colosseum_20200716.R
import kr.co.tjoeun.colosseum_20200716.datas.Notification
import kr.co.tjoeun.colosseum_20200716.datas.Topic
import kr.co.tjoeun.colosseum_20200716.utils.TimeUtil

class NotificationAdapter(
    val mContext:Context,
    val resId:Int,
    val mList:List<Notification>) : ArrayAdapter<Notification>(mContext, resId, mList) {

    val inf = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var tempRow = convertView
        if (tempRow == null) {
            tempRow = inf.inflate(R.layout.notification_list_item, null)
        }

        val row = tempRow!!

        val notiTxt = row.findViewById<TextView>(R.id.notiTxt)
        val notiTimeTxt = row.findViewById<TextView>(R.id.notiTimeTxt)

        val data = mList[position]

        notiTxt.text = data.title
        notiTimeTxt.text = TimeUtil.getTimeAgoFromCalendar(data.createAtCal)


        return row

    }

}