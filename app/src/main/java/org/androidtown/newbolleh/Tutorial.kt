package org.androidtown.newbolleh

import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_tutorial.*

class Tutorial : AppCompatActivity()
{

    val viewList = arrayListOf<View>()
    var viewIndex = -1

    inner class ViewPagerAdapter : PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return view == `object`
        }

        override fun getCount(): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            return viewList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var temp = viewList[position]
            pager.addView(temp)
            return temp
        }



        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            pager.removeView(`object` as View)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val infoFirst = 1;
        val sf = this.getSharedPreferences("Welcome", Context.MODE_PRIVATE)
        val edit = sf.edit()
        edit.putInt("First", infoFirst)
        edit.commit()


        init()

    }

    fun init(){
        t_skip.setOnClickListener {
            finish()
        }
        t_start.setOnClickListener {
            finish()
        }

        viewList.add(layoutInflater.inflate(R.layout.page1 , null))
        viewList.add(layoutInflater.inflate(R.layout.page2 , null))
        viewList.add(layoutInflater.inflate(R.layout.page3 , null))
        viewList.add(layoutInflater.inflate(R.layout.page4 , null))
        viewList.add(layoutInflater.inflate(R.layout.page5 , null))

        pager.adapter = ViewPagerAdapter()
        circle_indicator.setupWithViewPager(pager)
    }

}
