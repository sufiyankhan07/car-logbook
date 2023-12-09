package com.cmtech.amslogbook.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmtech.amslogbook.R
import com.cmtech.amslogbook.databinding.ActivityPlacesBinding
import com.cmtech.amslogbook.databinding.PlaceListBinding
import com.cmtech.amslogbook.global.DB
import com.cmtech.amslogbook.global.PubFun
import com.cmtech.amslogbook.model.Trip_list

 data class places(val ID:String="",val name:String="",val address:String="")
class placesActivity:AppCompatActivity(), SearchView.OnQueryTextListener,placeAdapter.onitemclick {
    lateinit var binding:ActivityPlacesBinding
    val placeslist:ArrayList<places> = ArrayList()
    var adapter:placeAdapter?=null
    var db:DB?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Places"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fab.setOnClickListener{
            val intent=Intent(this,placesfiledActivity::class.java)
            startActivity(intent)
        }
        db= DB(this)
        loadlist()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadlist(){
        placeslist.clear()
        try {
            val sqlquery="SELECT ID, NAME,ADDRESS FROM PLACES ORDER BY NAME ASC"
            db?.FireQuery(sqlquery).let {
                if(it!!.count>0){
                    it.moveToFirst()
                    do{
                        val placelist=places(ID = PubFun.isCursorNULL(it,"ID",""),
                            name = PubFun.isCursorNULL(it,"NAME",""),
                            address = PubFun.isCursorNULL(it,"ADDRESS",""))
                        placeslist.add(placelist)
                    }while (it.moveToNext())
                    binding.recyclerView.visibility= View.VISIBLE
                    binding.nodatafound.visibility=View.GONE
                }else{
                    binding.recyclerView.visibility= View.GONE
                    binding.nodatafound.visibility=View.VISIBLE
                }
                binding.recyclerView.layoutManager=LinearLayoutManager(this)
                adapter= placeAdapter(this,placeslist,this)
                binding.recyclerView.adapter=adapter
                adapter!!.updatelist(placeslist)
                adapter!!.notifyDataSetChanged()
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search,menu)
        val search = menu.findItem(R.id.search)
        val mSearchView = search.actionView as SearchView
        mSearchView.queryHint = "Search in places"
        mSearchView.setOnQueryTextListener(this)
        mSearchView.maxWidth = Integer.MAX_VALUE
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        loadlist()
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        onQueryTextChange(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
       applyFilter(newText.toString().trim())
        return false
    }
    private fun applyFilter(searchValue: String) {
        val temp:ArrayList<places> = ArrayList()
        if(placeslist.size>0){
            for(list in placeslist){
                if(list.ID.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.name.toLowerCase().contains(searchValue.toLowerCase()) ||
                    list.address.toLowerCase().contains(searchValue.toLowerCase()))
                {
                    temp.add(list)
                }
            }
            adapter?.updatelist(temp)
        }
    }

    override fun onitemClick(id: String) {
        super.onitemClick(id)
        val intent=Intent(this,AddFuelActivity::class.java)
        intent.putExtra("place",id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
class placeAdapter(val context: Context, var items:ArrayList<places>, val callback: onitemclick):RecyclerView.Adapter<placeAdapter.viewholder>(){


    class viewholder(view:View): RecyclerView.ViewHolder(view) {
        val binding=PlaceListBinding.bind(view)

    }
    interface onitemclick{
        fun onitemClick(id:String){}
    }

    fun updatelist(item:ArrayList<places>){
        this.items=item
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding=PlaceListBinding.inflate(LayoutInflater.from(context),parent,false)
        return viewholder(binding.root)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.binding.resources.text=items[position].ID
        holder.binding.name.text=items[position].name
        holder.binding.placesId.setOnClickListener {
            callback.onitemClick(items[position].ID)
        }


    }

}



