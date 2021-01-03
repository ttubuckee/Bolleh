package org.androidtown.newbolleh.Datas.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.androidtown.newbolleh.R

class routeBaseAdapter(val context: Context, val map:MutableMap<String,String>, val keySet:MutableList<String>): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view:View=LayoutInflater.from(context).inflate(R.layout.route_list,null)
        val nameText=view.findViewById<TextView>(R.id.routeUserName)
        val pathText=view.findViewById<TextView>(R.id.routeUserPath)
        nameText.text=keySet[p0]
        pathText.text=map[keySet[p0]]
        return view
    }

    override fun getItem(p0: Int): Any {
        return keySet[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return map.size
    }
}