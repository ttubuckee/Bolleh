package org.androidtown.newbolleh

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import org.androidtown.newbolleh.Datas.makeRoom.Decisionroom_response

class DecisionAdapter(
    var context: Context,
    var listener: decisionListener,
    var list: List<Decisionroom_response.stations>
) : RecyclerView.Adapter<DecisionAdapter.ViewHolder>() {
    interface decisionListener {
        fun onclick(view: View)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView_rank: TextView
        var textView_station: TextView
        var textView_avg: TextView
        var textView_dev: TextView
        var linear_select: LinearLayout

        init {
            textView_rank = view.findViewById(R.id.textView_rank)
            textView_station = view.findViewById(R.id.textView_station)
            textView_avg = view.findViewById(R.id.textView_avg)
            textView_dev = view.findViewById(R.id.textView_dev)
            linear_select = view.findViewById(R.id.Linear_select)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DecisionAdapter.ViewHolder {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.decision_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DecisionAdapter.ViewHolder, position: Int) {
        holder.textView_rank.text = list[position].rank
        holder.textView_station.text = list[position].place

        var str = list[position].avg.split(",")
        holder.textView_avg.text = str[0]
        if(str[1] != ""){
            holder.textView_dev.text = str[1]
        }

        holder.linear_select.setOnClickListener {
            listener.onclick(it)
        }
    }
}