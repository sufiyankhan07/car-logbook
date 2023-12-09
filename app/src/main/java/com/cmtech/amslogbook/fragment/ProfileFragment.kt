package com.cmtech.amslogbook.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.activity.UpdateProfileActivity
import com.cmtech.amslogbook.databinding.FragmentProfileBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.example.mypracticamstestko.Manager.SessionManager
import com.example.mypracticamstestko.Manager.SharedPrefs
import com.example.mypracticamstestko.Manager.SharedPrefs.Companion.SELECTED_WJFILE_ID

class ProfileFragment : Fragment() {
    lateinit var binding:FragmentProfileBinding
    var db:DB?=null
    var Manager:SessionManager?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?  ): View? {
        binding=FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db=activity?.let { DB(it) }
        Manager=activity?.let { SessionManager(it) }
        binding.editProfile.setOnClickListener{
            val intent=Intent(activity,UpdateProfileActivity::class.java)
            startActivity(intent)
        }
        getImage()

    }

    override fun onResume() {
        super.onResume()
        getImage()
    }

    private fun getImage(){
        val sharedpref=SharedPrefs.getString(requireContext(), SELECTED_WJFILE_ID)
        if(sharedpref!=null){
            Glide.with(requireContext())
                .load(sharedpref)
                .placeholder(R.drawable.user).error(R.drawable.user)
                .into(binding.profiltImg)
        }
    }


}
