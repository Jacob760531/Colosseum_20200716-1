package kr.co.tjoeun.colosseum_20200716.adapters

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kr.co.tjoeun.colosseum_20200716.R
import kr.co.tjoeun.colosseum_20200716.ViewReplyDetailActivity
import kr.co.tjoeun.colosseum_20200716.datas.Reply
import kr.co.tjoeun.colosseum_20200716.utils.ServerUtil
import kr.co.tjoeun.colosseum_20200716.utils.TimeUtil
import org.json.JSONObject
import java.text.SimpleDateFormat

class ReplyAdapter(
    val mContext:Context,
    resId: Int,
    val mList:List<Reply>) : ArrayAdapter<Reply>(mContext, resId, mList) {

    val inf = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var tempRow = convertView

        if (tempRow == null) {
            tempRow = inf.inflate(R.layout.reply_list_item, null)
        }

        val row = tempRow!!

        val writerNickNameTxt = row.findViewById<TextView>(R.id.writerNickNameTxt)
        val selectedSideTitleTxt = row.findViewById<TextView>(R.id.selectedSideTitleTxt)
        val contentTxt = row.findViewById<TextView>(R.id.contentTxt)

//        시간 정보 텍스트뷰
        val replyWriteTimeTxt = row.findViewById<TextView>(R.id.replyWriteTimeTxt)

//        버튼들 추가 => 좋/싫/답글
        val replyBtn = row.findViewById<Button>(R.id.replyBtn)
        val likeBtn = row.findViewById<Button>(R.id.likeBtn)
        val dislikeBtn = row.findViewById<Button>(R.id.dislikeBtn)

        val data = mList[position]

        writerNickNameTxt.text = data.writer.nickName
        selectedSideTitleTxt.text = "(${data.selectedSide.title})"
        contentTxt.text = data.content

//        시간정보 텍스트뷰 내용 설정 => 방금 전, ?분 전, ?시간 전 등등..

        replyWriteTimeTxt.text = TimeUtil.getTimeAgoFromCalendar(data.writtenDateTime)


//        날짜 출력 양식용 변수
//        val sdf = SimpleDateFormat("yy-MM-dd a h시 m분")
//
//        replyWriteTimeTxt.text = sdf.format(data.writtenDateTime.time)


//        좋/싫/답글 갯수 반영

        likeBtn.text = "좋아요 ${data.likeCount}"
        dislikeBtn.text = "싫어요 ${data.dislikeCount}"
        replyBtn.text = "답글 ${data.replyCount}"

//        내 좋아요 여부 반영
        if(data.myLike) {
//            좋아요버튼의 배경을 => red_border_box로 변경
            likeBtn.setBackgroundResource(R.drawable.red_border_box)
//            좋아요 버튼 글씨 => naverRed로 변경
            likeBtn.setTextColor(mContext.resources.getColor(R.color.naverRed))

        } else {
//            좋아요버튼의 배경을 => gray_border_box로 변경
            likeBtn.setBackgroundResource(R.drawable.gray_border_box)
//            좋아요를 안찍었다면 gray로 돌려줘야함
            likeBtn.setTextColor(mContext.resources.getColor(R.color.textGray))
        }

//        내 싫어요 여부 반영
        if(data.myDisLike) {
//            좋아요버튼의 배경을 => red_border_box로 변경
            dislikeBtn.setBackgroundResource(R.drawable.blue_border_box)
            dislikeBtn.setTextColor(mContext.resources.getColor(R.color.naverBlue))
        } else {
//            좋아요버튼의 배경을 => gray_border_box로 변경
            dislikeBtn.setBackgroundResource(R.drawable.gray_border_box)
            dislikeBtn.setTextColor(mContext.resources.getColor(R.color.textGray))
        }

//        답글 버튼이 눌리면 => 의견 상세 화면으로 진입
        replyBtn.setOnClickListener {

            val myIntent = Intent(mContext, ViewReplyDetailActivity::class.java)
//            startActivity 함수는 AppCompatActivity가 내려주는 기능.
//            Adapter는 액티비티가 아니므로, startActivity 기능을 내려주지 않는다.
//            mContext 변수가, 어떤 화면이 리스트뷰를 뿌리는지 들고 있다.
//            mContext를 이용해서 액티비티를 열어주자.

//            몇번 의견에 대한 상세를 보고싶은지 id만 넘겨주자
//            해당 화면에서 다시 서버를 통해 데이터를 받아오자.
            myIntent.putExtra("replyId", data.id)

            mContext.startActivity(myIntent)

        }

//        의견에 대한 좋아요 / 싫어요 버튼 클릭 이벤트

        likeBtn.setOnClickListener {

            ServerUtil.postRequestReplyLickOrDislike(mContext,data.id, true, object : ServerUtil.JsonResponseHandler{
                override fun onResponse(json: JSONObject) {
//                    변경된 좋아요 갯수 / 싫어요 갯수를 파악해서 버튼 문구를 새로고침
//                    목록에 뿌려지는 data의 좋아요/싫어요 갯수를 변경

                    val dataObj = json.getJSONObject("data")
                    val replyObj = dataObj.getJSONObject("reply")

                    val reply = Reply.getReplyFromJson(replyObj)

//                    이미 화면에 뿌려져 있는 data의 내용만 교체

                    data.likeCount = reply.likeCount
                    data.dislikeCount = reply.dislikeCount

//                    좋아요를 찍었는지 아닌지 체크
                    data.myLike = reply.myLike
                    data.myDisLike = reply.myDisLike


//                    data의 값이 변경 => 리스트뷰를 구성하는 목록에 변경 => 어댑터.notifiDataSet 실행
//                    어댑터 내부에 내장되어있으니 호출만 하면 끝

//                    새로고침 => UI변경 => runOnUiThread 등으로 UI 쓰레드로 처리해야함
//                    어댑터는 runOnUiThread 기능이 없다.

//                    Handler 을 이용해서 => UI쓰레드에 접근하자

                    var uiHandler = Handler(Looper.getMainLooper())

                    uiHandler.post {
                        notifyDataSetChanged()

//                        서버가 알려주는 메세지를 토스트로 출력
                        val message = json.getString("message")

                        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show()
                    }




                }
            } )
        }

//        싫어요 버튼 누르면 => 서버전달 + 갯수 반영 / 토스트 출력

        dislikeBtn.setOnClickListener {

            ServerUtil.postRequestReplyLickOrDislike(mContext,data.id,false, object : ServerUtil.JsonResponseHandler{
                override fun onResponse(json: JSONObject) {

                    val dataObj = json.getJSONObject("data")
                    val replyObj = dataObj.getJSONObject("reply")

                    val reply = Reply.getReplyFromJson(replyObj)

//                    이미 화면에 뿌려져 있는 data의 내용만 교체

                    data.likeCount = reply.likeCount
                    data.dislikeCount = reply.dislikeCount
                    data.myLike = reply.myLike
                    data.myDisLike = reply.myDisLike

                    val uiHandler = Handler(Looper.getMainLooper())

                    uiHandler.post {
                        notifyDataSetChanged()
                        val message = json.getString("message")
                        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show()

                    }

                }

            })
        }


        return row
    }

}