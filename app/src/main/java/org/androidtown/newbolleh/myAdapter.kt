package org.androidtown.newbolleh

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.androidtown.newbolleh.Datas.history.History_detail_response

class myAdapter(context: Context,val resource:Int,var list:ArrayList<History_detail_response.users>)
    : ArrayAdapter<History_detail_response.users>(context,resource,list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v:View? = convertView

        if(v == null){
            val vi = context.applicationContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v= vi.inflate(R.layout.userlist,null)
        }

        val p = list.get(position)
        v!!.findViewById<TextView>(R.id.nickName).text = p.nick
        v!!.findViewById<TextView>(R.id.startStation).text = p.start
        return v

//        return super.getView(position, convertView, parent)
    }
}