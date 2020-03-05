package com.dapcasillas.locationtracker.Activities.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.dapcasillas.locationtracker.Data.User
import com.dapcasillas.locationtracker.R
import kotlinx.android.synthetic.main.row_user.view.*
import java.util.ArrayList

class UsersAdapter  (private var context: Context, var rowLayout: Int, var  usersList: MutableList<User>) : androidx.recyclerview.widget.RecyclerView.Adapter<UsersAdapter.ViewHolder>(),

    Filterable {
    private var usersListFiltered: MutableList<User>? = null

    lateinit var itemClickListener: OnItemClickListener


    init {
        this.usersListFiltered = usersList

    }

    override fun getItemCount(): Int {
        return usersListFiltered!!.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersListFiltered!![position]

        try {

            val locations = user.location?.latitude.toString() + ", " + user.location?.longitude.toString()
            holder.itemView.tv_name.setText(user.name)
            holder.itemView.tv_email.setText(user.email)
            holder.itemView.tv_location.setText(locations)
            holder.itemView.tv_distance.setText("Distancia")//user.name)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.placeCardUser.setOnClickListener(this)
        }

        override fun onClick(view: View) = itemClickListener.onItemClick(itemView)//,
//            usersList[position].idSitioWeb,
//            usersList[position].idUsuario,
//            usersList[position].sitioWeb)



    }


    interface OnItemClickListener {
        fun onItemClick(view: View)//, idSitioWeb: Int?, idUsuario: Int?, sitioWeb: String?)
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val charString = charSequence?.toString()
                if (charString!!.isEmpty()) {
                    usersListFiltered = usersList

                } else {
                    /*val filteredList = ArrayList<Product.Product>()
                    for (row in productsList) {
                        if (row.name!!.toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row)
                        }
                    }

                    productListFiltered = filteredList*/
                }

                /*val filterResults = Filter.FilterResults()
                filterResults.values = productListFiltered
                */
                return Filter.FilterResults()
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                usersListFiltered = null
                //   productListFiltered = filterResults.values as ArrayList<Product.Product>
                notifyDataSetChanged()
            }
        }
    }

}
