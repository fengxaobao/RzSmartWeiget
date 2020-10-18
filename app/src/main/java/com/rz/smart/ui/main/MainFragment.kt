package com.rz.smart.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.orhanobut.logger.Logger
import com.rz.smart.R
import com.rz.smart.model.MainViewModel
import com.rz.smart.model.entity.CuisineInfo
import com.rz.smart.ui.adapter.GoodsAdapter
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getAllCuisine()
        viewModel.allCuisineInfoLiveData.observe(viewLifecycleOwner,
            Observer<List<CuisineInfo>> {
                Logger.e(it.toString())
                menuRecyclerView.layoutManager = GridLayoutManager(activity,4)
                menuRecyclerView.adapter= GoodsAdapter(it)
            })
    }

}