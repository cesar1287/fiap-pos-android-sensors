package com.github.cesar1287.sensores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class StepsListAdapter(
    private val context: Context,
    private val mStepCountList: ArrayList<Step>
) : BaseAdapter() {

    override fun getCount(): Int {
        return mStepCountList.size
    }

    override fun getItem(position: Int): Any {
        return mStepCountList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var inflateView: View? = convertView
        if (inflateView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            inflateView = layoutInflater.inflate(
                R.layout.step_item,
                parent,
                false
            )
        }

        val tvDate = inflateView?.findViewById(R.id.tvDate) as TextView
        val tvStep = inflateView.findViewById(R.id.tvStep) as TextView

        tvDate.text = mStepCountList.get(position).mDate.toString()
        tvStep.text = "${mStepCountList.get(position).mStepCount} passos"

        return inflateView
    }
}

