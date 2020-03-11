package com.example.xiaozhejun

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), OnClickListener {

    companion object {
        const val TAG = "MainActivity"
        private const val OFF_SCREEN_PAGE_LIMIT = 2
    }

    private var mAdapter: SimpleViewPagerAdapter = SimpleViewPagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_insert.setOnClickListener(this@MainActivity)
        btn_delete.setOnClickListener(this@MainActivity)
        tv_items_size.text = "数据列表长度：${mAdapter.count}."
        view_pager_test.adapter = mAdapter
        view_pager_test.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tv_current_position.text = "当前选择的位置：$position"
            }

        })
        view_pager_test.offscreenPageLimit = OFF_SCREEN_PAGE_LIMIT
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_insert -> {
                edt_position.text.toString().toIntOrNull()?.let { position ->
                    if (position < mAdapter.count) {
                        mAdapter.itemList.add(position, edt_element.text.toString())
                        mAdapter.notifyDataSetChanged()
                        tv_items_size.text = "数据列表长度：${mAdapter.count}."
                    }
                }
            }

            R.id.btn_delete -> {
                edt_position.text.toString().toIntOrNull()?.let { position ->
                    if (position < mAdapter.count) {
                        mAdapter.itemList.removeAt(position)
                        mAdapter.notifyDataSetChanged()
                        tv_items_size.text = "数据列表长度：${mAdapter.count}."
                    }
                }
            }
        }
    }

    /**
     * ViewPager适配器
     * */
    class SimpleViewPagerAdapter : PagerAdapter() {

        val itemList = ArrayList<String>().apply {
            add("0")
            add("1")
            add("2")
            add("3")
            add("4")
            add("5")
            add("6")
            add("7")
            add("8")
            add("9")
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object` as? View
        }

        override fun getCount(): Int {
            return itemList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = LayoutInflater.from(container?.context).inflate(R.layout.item_view_pager, container, false)
            val itemText: TextView? = itemView.findViewById(R.id.tv_item_view_pager) as? TextView
            itemText?.text = itemList[position]
            itemView.tag = position
            container?.addView(itemView)
            Log.d(TAG, "instantiateItem ${itemView?.tag as? Int}.")
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val itemView = `object` as? View
            container?.removeView(itemView)
            Log.d(TAG, "destroyItem ${itemView?.tag as? Int}.")
        }

        /**
         * ViewPager适配器调用notifyDataSetChanged后默认不会刷新UI
         * getItemPosition的调用次数与offscreenPageLimit有关，
         * 只遍历以当前元素为中心，偏移位置在offscreenPageLimit内的元素
         * 调用次数 == 2 × offscreenPageLimit + 1
         * */
        override fun getItemPosition(`object`: Any): Int {
            val itemView = `object` as? View
            Log.d(TAG, "getItemPosition ${itemView?.tag as? Int}.")
            // POSITION_NONE能实刷新ViewPager中的位置信息，但是会调用2 × offscreenPageLimit + 1 次 destroyItem和instantiateItem
            // POSITION_UNCHANGED无法实时刷新ViewPager中的位置信息，不会调用instantiateItem 和 destroyItem
            // 具体逻辑看 android.support.v4.view.ViewPager.dataSetChanged
            return POSITION_NONE
        }
    }
}
