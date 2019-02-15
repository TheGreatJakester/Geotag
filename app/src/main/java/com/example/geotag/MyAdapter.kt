package com.example.geotag

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


class MyAdapter(private val myDataset: MutableList<Tag>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        // set the view's size, margins, paddings and layout parameter
        //...

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.view.findViewById<TextView>(R.id.latText).text = myDataset[position].location.latitude.toString()
        holder.view.findViewById<TextView>(R.id.longText).text = myDataset[position].location.longitude.toString()
        holder.view.findViewById<TextView>(R.id.altText).text = myDataset[position].location.altitude.toString()
        holder.view.findViewById<TextView>(R.id.buldingText).text = myDataset[position].building
        holder.view.findViewById<TextView>(R.id.floorText).text = myDataset[position].floor.toString()
        holder.view.findViewById<TextView>(R.id.roomText).text = myDataset[position].room
        holder.view.findViewById<TextView>(R.id.descriptionText).text = myDataset[position].description

        holder.view.findViewById<Button>(R.id.trashTagButton).setOnClickListener {
            myDataset.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}