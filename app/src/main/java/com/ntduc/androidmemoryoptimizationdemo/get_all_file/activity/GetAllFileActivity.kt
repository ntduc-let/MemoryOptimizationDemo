package com.ntduc.androidmemoryoptimizationdemo.get_all_file.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ntduc.androidmemoryoptimizationdemo.R
import com.ntduc.androidmemoryoptimizationdemo.databinding.ActivityGetAllFileBinding
import com.ntduc.androidmemoryoptimizationdemo.get_all_file.adapter.GetAllFileAdapter
import com.ntduc.recyclerviewutils.sticky.StickyHeadersLinearLayoutManager

class GetAllFileActivity : AppCompatActivity() {
    companion object{
        var adapter: GetAllFileAdapter? = null
    }

    private lateinit var binding: ActivityGetAllFileBinding
//    private lateinit var adapter: GetAllFileAdapter
    private lateinit var viewModel: GetAllFileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetAllFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadAllFile(this)
    }

    private fun init() {
        initView()
        initData()
    }

    private fun initData() {
        viewModel.listAllFile.observe(this) {
            if (viewModel.isLoadListAllFile) {
                binding.layoutLoading.root.visibility = View.GONE
                if (it.isEmpty()) {
                    binding.layoutNoItem.root.visibility = View.VISIBLE
                    binding.rcvList.visibility = View.INVISIBLE
                } else {
                    binding.layoutNoItem.root.visibility = View.GONE
                    binding.rcvList.visibility = View.VISIBLE
                    adapter?.updateData(it)
                }
            } else {
                binding.layoutLoading.root.visibility = View.VISIBLE
            }
        }
    }

    private fun initView() {
        viewModel = ViewModelProvider(this)[GetAllFileViewModel::class.java]

        adapter = GetAllFileAdapter(this)
        binding.rcvList.adapter = adapter
        binding.rcvList.setHasFixedSize(true)
        val layoutManager: StickyHeadersLinearLayoutManager<GetAllFileAdapter> =
            StickyHeadersLinearLayoutManager(this)
        binding.rcvList.layoutManager = layoutManager
    }
}