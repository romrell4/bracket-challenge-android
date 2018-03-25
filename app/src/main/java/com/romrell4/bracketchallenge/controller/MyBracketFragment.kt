package com.romrell4.bracketchallenge.controller

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.bracketchallenge.R

/**
 * Created by romrell4 on 3/25/18
 */
class MyBracketFragment: Fragment() {
    companion object {
        fun newInstance(): MyBracketFragment {
            val fragment = MyBracketFragment()
            //TODO: Put together bundle
            fragment.arguments = Bundle()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_my_bracket, container, false)
        //TODO: Actually set up view
        return view
    }
}