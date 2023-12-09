package com.cmtech.amslogbook.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.FragmentExpensesBinding
import com.cmtech.amslogbook.global.DB

class ExpensesFragment : Fragment() {
    lateinit var binding:FragmentExpensesBinding
    var adapter:ArrayAdapter<CharSequence>?=null
    var db:DB?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentExpensesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db= activity?.let {DB(it) }
        init()

    }
    fun init(){
        adapter= activity?.let {
            ArrayAdapter.createFromResource(it,R.array.Graph,android.R.layout.simple_spinner_item)
        }
        adapter?.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spinner.adapter=adapter
    }
}