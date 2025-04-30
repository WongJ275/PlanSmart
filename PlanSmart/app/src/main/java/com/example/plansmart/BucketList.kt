package com.example.plansmart

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Button

class BucketList : AppCompatActivity() {
    private var btn_addList: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bucket_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val list = arrayListOf<MutableMap<String, Any>>()
        val map: MutableMap<String, Any> = HashMap()
        val count = 0

        map["Title"] = "TestTitle $count"
        map["Info"] = "TestInfo"
        list.add(map)

        val adapter = SimpleAdapter(this, list, R.layout.bucket_list_item,
            arrayOf("Title", "Info"), intArrayOf(R.id.title, R.id.info))
        val listView:ListView = findViewById(R.id.listView)
        listView.adapter = adapter

        btn_addList = findViewById(R.id.addBucketListBtn)
        btn_addList!!.setOnClickListener {
            map["Title"] = "TestTitle $count"
            map["Info"] = "TestInfo"
            list.add(map)
            adapter.notifyDataSetChanged()
        }

    }
}