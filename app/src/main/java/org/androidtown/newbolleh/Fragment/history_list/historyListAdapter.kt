package org.androidtown.newbolleh.Fragment.history_list

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.androidtown.newbolleh.R

class historyListAdapter(val context: Context, val itemlist:ArrayList<items>):BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.history_list_item, null)

        val title = view.findViewById<TextView>(R.id.textView_title)
        val duedate = view.findViewById<TextView>(R.id.textView_duedate)
        val status = view.findViewById<TextView>(R.id.textView_status)
        val place = view.findViewById<TextView>(R.id.textView_place)
        val pin = view.findViewById<TextView>(R.id.textView_pin)

        val item = itemlist[p0]
        title.text = item.title
        duedate.text = item.duedate

        if(item.status == 1) {
            status.text = "대기중"
            status.setTextColor(Color.GREEN)
        }
        else if(item.status ==2){
            status.text = "결정됨"
            status.setTextColor(Color.RED)
        }

        if (item.status ==2 || item.type ==1)
            place.text = item.place
        else
            place.text = ""

        pin.text = item.pin.toString()

        return view
    }

    override fun getItem(p0: Int): Any {
        return itemlist[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return itemlist.size
    }
}