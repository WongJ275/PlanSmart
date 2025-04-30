package com.example.plansmart

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

public class TagAdapter (var tagList: MutableList<Tag>, var list: ArrayList<MutableMap<String, Any>>, var tempList: ArrayList<MutableMap<String, Any>> , var adapter: SimpleAdapter) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var chosenPosition = -1
    var currentSortSelection = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tagName = holder.itemView.findViewById<TextView>(R.id.tagName)

        tagName.text = tagList[position].name

        val tag = tagList[position]
        /*tagName.setBackgroundColor(if (tag.chosen) Color.WHITE else Color.TRANSPARENT)
        tagName.setTextColor(if (tag.chosen) Color.WHITE else Color.BLACK)*/

        if (tag.chosen) {
            tagName.setBackgroundResource(R.drawable.round_corner_background)
            tagName.setTextColor(Color.WHITE)
        }
        else {
            tagName.setBackgroundColor(Color.TRANSPARENT)
            tagName.setTextColor(Color.BLACK)
        }

        tagName.setOnClickListener {

            val pos = holder.adapterPosition

            if (chosenPosition >= tagList.size) {
                chosenPosition = -1
            }

            if (chosenPosition != -1 && chosenPosition != pos) {
                tagList[chosenPosition].chosen = false

                notifyItemChanged(chosenPosition)

                chosenPosition = pos
                tag.chosen = !tag.chosen
            }
            else if (chosenPosition == -1) {
                chosenPosition = pos
                tag.chosen = true
            }
            else if (chosenPosition == pos) {
                tag.chosen = !tag.chosen
                chosenPosition = -1
            }
            notifyItemChanged(pos)


            if (chosenPosition != -1) {
                list.clear()

                val chosenTag = tagList[chosenPosition].name

                for (task in tempList) {
                    if (task["Tag0"] == chosenTag || task["Tag1"] == chosenTag || task["Tag2"] == chosenTag ) {
                        list.add(task)
                    }
                }
            }
            else if (chosenPosition == -1) {
                list.clear()

                for (task in tempList) {
                    list.add(task)
                }
            }
            Log.d("tagsort", currentSortSelection.toString())
            adapter.notifyDataSetChanged()
            sortList(list, currentSortSelection, adapter)
        }

    }

    fun sortList (list: ArrayList<MutableMap<String, Any>>, currentSortSelection: Int, adapter: SimpleAdapter){
        when (currentSortSelection) {
            0 -> list.sortWith(dateComparator)
            1 -> list.sortWith(dateComparator)
            2 -> list.sortWith(levelComparator)
            3 -> list.sortWith(levelComparator)
            4 -> list.sortWith(titleComparator)
            5 -> list.sortWith(titleComparator)
        }
        if (currentSortSelection % 2 == 1) list.reverse()
        adapter.notifyDataSetChanged()
    }
    val dateComparator = Comparator<MutableMap<String, Any>> { m1, m2 ->
        val d1 = (m1["Date"] as String).split("-")
        val d2 = (m2["Date"] as String).split("-")
        when {
            (d1[0].trim().toInt() > d2[0].trim().toInt()) -> 1
            (d1[0].trim().toInt() < d2[0].trim().toInt()) -> -1
            (d1[1].trim().toInt() > d2[1].trim().toInt()) -> 1
            (d1[1].trim().toInt() < d2[1].trim().toInt()) -> -1
            (d1[2].trim().toInt() > d2[2].trim().toInt()) -> 1
            (d1[2].trim().toInt() < d2[2].trim().toInt()) -> -1
            else -> 0
        }
    }
    val levelComparator = Comparator<MutableMap<String, Any>> { m1, m2 ->
        val l1 = (m1["Level"] as String).toInt()
        val l2 = (m2["Level"] as String).toInt()
        when {
            l1 > l2 -> 1
            l1 < l2 -> -1
            else -> 0
        }
    }
    val titleComparator = Comparator<MutableMap<String, Any>> { m1, m2 ->
        val t1 = (m1["Title"] as String)
        val t2 = (m2["Title"] as String)
        when {
            t1 > t2 -> 1
            t1 < t2 -> -1
            else -> 0
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }
}