package com.example.plansmart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Integer.max

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

//firebase
private lateinit var auth: FirebaseAuth
private lateinit var db: FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [bucket_list.newInstance] factory method to
 * create an instance of this fragment.
 */
class bucket_list : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var btn_addList: Button? = null
    private var btn_backButton: Button? = null
    private var btn_editButton: Button? = null
    private var btn_add: Button? = null
    private var btn_cancelList: Button? = null
    private var btn_save_: Button? = null
    private var btn_cancelList_: Button? = null
    private var btn_deleteItem: FloatingActionButton? = null

    private var layout_greyBackground: ConstraintLayout? = null
    private var layout_addListBlock: LinearLayout? = null
    private var layout_editListBlock: ConstraintLayout? = null

    private var toastMessage: Toast? = null
    private var isShowingItems: Boolean = false
    private var currentBucketListId: Int = -1
    private var currentItemIndex: Int = -1
    private var bucketListTextView: TextView? = null
    private var addHeadingTextView: TextView? = null
    private var editHeadingTextView: TextView? = null

    private var isEditingItem: Boolean = false

    private var titleTextView: TextView? = null
    private var infoTextView: TextView? = null
    private var titleTextView_: TextView? = null
    private var infoTextView_: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        //firebase
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bucket_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = arrayListOf<MutableMap<String, Any>>()
        val bucketList = arrayListOf<MutableMap<String, Any>>()

        val ctx: Context = view.context
        val gson = Gson()
        val filename = "bucketList.json"

        //firebase
        auth = Firebase.auth
        val db = Firebase.firestore

        // store lists of things
        // MutableMap of {id, Pair of {MutableMap of listInfo, List of MutableMap of itemInfo}}
        // save load this
        var itemLists : MutableMap<Int, Pair<MutableMap<String, Any>, ArrayList<MutableMap<String, Any>>>> //= mutableMapOf<Int, Pair<MutableMap<String, Any>, ArrayList<MutableMap<String, Any>>>>()

        try {
            ctx.openFileInput(filename).bufferedReader().use { inputStream ->
                itemLists = gson.fromJson(inputStream, object : TypeToken<MutableMap<Int, Pair<MutableMap<String, Any>, ArrayList<MutableMap<String, Any>>>>>() {}.type)
            }
            Log.d("myTag", itemLists.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            itemLists = mutableMapOf<Int, Pair<MutableMap<String, Any>, ArrayList<MutableMap<String, Any>>>>()
        }

        var count = 0

        btn_backButton = requireView().findViewById(R.id.bucketListBackButton)
        btn_editButton = requireView().findViewById(R.id.bucketListEditButton)
        btn_add = requireView().findViewById(R.id.addBtn)
        btn_addList = requireView().findViewById(R.id.bucketListAddBtn)
        btn_cancelList = requireView().findViewById(R.id.bucketListCancelBtn)
        btn_save_ = requireView().findViewById(R.id.bucketListSaveBtn)
        btn_cancelList_ = requireView().findViewById(R.id.bucketListCancelBtn_)
        btn_deleteItem = requireView().findViewById(R.id.itemDeleteBtn)
        bucketListTextView = requireView().findViewById(R.id.bucketListTextView)
        addHeadingTextView = requireView().findViewById(R.id.textViewAddHeading)
        editHeadingTextView = requireView().findViewById(R.id.textViewEditHeading)
        layout_greyBackground = requireView().findViewById(R.id.greyBackground)
        layout_addListBlock = requireView().findViewById(R.id.bucketListAddBlock)
        layout_editListBlock = requireView().findViewById(R.id.editBlock)

        titleTextView = requireView().findViewById(R.id.editTextTitle)
        infoTextView = requireView().findViewById(R.id.editTextInfo)
        titleTextView_ = requireView().findViewById(R.id.editTextTitle_)
        infoTextView_ = requireView().findViewById(R.id.editTextInfo_)

        val adapter = SimpleAdapter(activity, list, R.layout.bucket_list_item,
            arrayOf("Title", "Info"), intArrayOf(R.id.title, R.id.info))
        val listView: ListView = requireView().findViewById(R.id.listView)
        listView.adapter = adapter


        for (key in itemLists.keys) {
            //list.add(itemLists[key]!!.first)
            val map: MutableMap<String, Any> = HashMap()
            map["Title"] = itemLists[key]?.first?.get("Title").toString()
            map["Info"] = itemLists[key]?.first?.get("Info").toString()
            map["Id"] = itemLists[key]?.first?.get("Id").toString().toFloat().toInt()
            count = max(itemLists[key]?.first?.get("Id").toString().toFloat().toInt() + 1, count)
            list.add(map)
            Log.d("myTag", itemLists[key]!!.first.toString())
        }
        adapter.notifyDataSetChanged()

        isShowingItems = false
        listView.setOnItemClickListener { adapterV, v, i, l ->
            if (!isShowingItems)
            {
                isEditingItem = false
                val item: HashMap<String, *> = adapterV.getItemAtPosition(i) as HashMap<String, *>
                //Log.d("myTag", item.toString())
                currentBucketListId = item?.get("Id").toString().toInt()
                if (toastMessage!= null) {
                    toastMessage!!.cancel()
                }
                toastMessage = Toast.makeText(activity, item?.get("Title").toString(), Toast.LENGTH_SHORT)
                //toastMessage!!.show()

                bucketList.clear()
                for (lists in list)
                {
                    bucketList.add(lists)
                }
                list.clear()
                adapter.notifyDataSetChanged()
                isShowingItems = true
                for (bucketItem in itemLists[currentBucketListId]!!.second)
                {
                    list.add(bucketItem)
                }
                adapter.notifyDataSetChanged()

                btn_backButton?.visibility = View.VISIBLE
                btn_editButton?.visibility = View.VISIBLE
                val displayTitle = "Bucket List > ${item?.get("Title").toString()}"
                bucketListTextView?.text = displayTitle
            }
            else
            {
                isEditingItem = true
                layout_greyBackground!!.visibility = View.VISIBLE
                layout_addListBlock!!.visibility = View.INVISIBLE
                layout_editListBlock!!.visibility = View.VISIBLE
                editHeadingTextView!!.text = "Edit Item"
                titleTextView_!!.text = itemLists.get(currentBucketListId)?.second?.get(i)?.get("Title").toString()
                infoTextView_!!.text = itemLists.get(currentBucketListId)?.second?.get(i)?.get("Info").toString()
                currentItemIndex = i
            }
        }

        btn_editButton!!.setOnClickListener {
            isEditingItem = false
            layout_greyBackground!!.visibility = View.VISIBLE
            layout_addListBlock!!.visibility = View.INVISIBLE
            layout_editListBlock!!.visibility = View.VISIBLE
            editHeadingTextView!!.text = "Edit List"
            Log.d("tag", currentBucketListId.toString() + " " + itemLists.toString())
            titleTextView_!!.text = itemLists.get(currentBucketListId)?.first?.get("Title").toString()
            infoTextView_!!.text = itemLists.get(currentBucketListId)?.first?.get("Info").toString()
        }

        btn_addList!!.setOnClickListener {
            val map: MutableMap<String, Any> = HashMap()
            if (!isShowingItems)
            {
                val t: String = (requireView().findViewById<EditText>(R.id.editTextTitle)!!).text.toString()
                if (t!="")
                {
                    map["Title"] = t
                    map["Info"] = (requireView().findViewById<EditText>(R.id.editTextInfo)!!).text.toString()
                    map["Id"] = count
                    list.add(map)
                    itemLists[count] = Pair(map, arrayListOf<MutableMap<String, Any>>())
                    count++
                }
                requireView().findViewById<EditText>(R.id.editTextTitle)!!.setText("")
                requireView().findViewById<EditText>(R.id.editTextInfo)!!.setText("")
            }
            else
            {
                val t: String = (requireView().findViewById<EditText>(R.id.editTextTitle)!!).text.toString()
                if (t!="")
                {
                    map["Title"] = t
                    map["Info"] = (requireView().findViewById<EditText>(R.id.editTextInfo)!!).text.toString()
                    list.add(map)
                    itemLists[currentBucketListId]?.second?.add(map)
                }
            }
            adapter.notifyDataSetChanged()


            val json = gson.toJson(itemLists)

            ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }

            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(filename.split(".")[0], json)
            }

            Log.d("myTag", itemLists.toString())
            //Log.d("myTag", json)
            layout_greyBackground!!.visibility = View.GONE
            layout_addListBlock!!.visibility = View.INVISIBLE
            layout_editListBlock!!.visibility = View.INVISIBLE

        }

        btn_add!!.setOnClickListener {
            if (!isShowingItems)
            {
                layout_greyBackground!!.visibility = View.VISIBLE
                layout_addListBlock!!.visibility = View.VISIBLE
                layout_editListBlock!!.visibility = View.INVISIBLE
                addHeadingTextView!!.text = "Add Bucket List"
            }
            else
            {
                layout_greyBackground!!.visibility = View.VISIBLE
                layout_addListBlock!!.visibility = View.VISIBLE
                layout_editListBlock!!.visibility = View.INVISIBLE
                addHeadingTextView!!.text = "Add Bucket Item"
            }
            requireView().findViewById<EditText>(R.id.editTextTitle)!!.setText("")
            requireView().findViewById<EditText>(R.id.editTextInfo)!!.setText("")
        }

        btn_deleteItem!!.setOnClickListener {
            if (isEditingItem)
            {
                list.removeAt(currentItemIndex)
                itemLists.get(currentBucketListId)?.second?.removeAt(currentItemIndex)
            }
            else
            {
                bucketList.removeAt(bucketList.indexOfFirst { x -> x["Id"] == currentBucketListId })
                itemLists.remove(currentBucketListId)
                if (isShowingItems)
                {
                    list.clear()
                    for (lists in bucketList)
                    {
                        list.add(lists)
                    }
                    adapter.notifyDataSetChanged()
                    isShowingItems = false

                    btn_backButton?.visibility = View.INVISIBLE
                    btn_editButton?.visibility = View.INVISIBLE
                    val displayText = "Bucket List"
                    bucketListTextView?.text = displayText
                }
            }

            layout_greyBackground!!.visibility = View.GONE
            layout_addListBlock!!.visibility = View.VISIBLE
            layout_editListBlock!!.visibility = View.INVISIBLE

            adapter.notifyDataSetChanged()

            val json = gson.toJson(itemLists)

            ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }

            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(filename.split(".")[0], json)
            }
        }

        btn_save_!!.setOnClickListener {
            val map: MutableMap<String, Any> = HashMap()
            if (!isEditingItem)
            {
                val t: String = titleTextView_!!.text.toString()
                if (t!="")
                {
                    itemLists.get(currentBucketListId)?.first?.set("Title", t)
                    itemLists.get(currentBucketListId)?.first?.set("Info", infoTextView_!!.text.toString())

                    bucketList.get(bucketList.indexOfFirst { x -> x["Id"] == currentBucketListId })["Title"] = t
                    bucketList.get(bucketList.indexOfFirst { x -> x["Id"] == currentBucketListId })["Info"] = infoTextView_!!.text.toString()

                    val displayTitle = "Bucket List > ${t.toString()}"
                    bucketListTextView?.text = displayTitle
                }
            }
            else
            {
                val t: String = titleTextView_!!.text.toString()
                if (t!="")
                {
                    itemLists.get(currentBucketListId)?.second?.get(currentItemIndex)?.set("Title", t)
                    itemLists.get(currentBucketListId)?.second?.get(currentItemIndex)?.set("Info", infoTextView_!!.text.toString())

                    list.get(currentItemIndex)["Title"] = t
                    list.get(currentItemIndex)["Info"] = infoTextView_!!.text.toString()
                }
            }
            adapter.notifyDataSetChanged()


            val json = gson.toJson(itemLists)

            ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }

            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(filename.split(".")[0], json)
            }

            Log.d("myTag", itemLists.toString())
            //Log.d("myTag", json)
            layout_greyBackground!!.visibility = View.GONE
            layout_addListBlock!!.visibility = View.INVISIBLE
            layout_editListBlock!!.visibility = View.INVISIBLE
        }

        btn_cancelList!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addListBlock!!.visibility = View.INVISIBLE
            layout_editListBlock!!.visibility = View.INVISIBLE
        }

        btn_cancelList_!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addListBlock!!.visibility = View.INVISIBLE
            layout_editListBlock!!.visibility = View.INVISIBLE
        }

        layout_addListBlock!!.setOnClickListener {

        }

        layout_editListBlock!!.setOnClickListener {

        }

        layout_greyBackground!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addListBlock!!.visibility = View.INVISIBLE
            layout_editListBlock!!.visibility = View.INVISIBLE
        }

        btn_backButton!!.setOnClickListener {
            if (isShowingItems)
            {
                list.clear()
                for (lists in bucketList)
                {
                    list.add(lists)
                }
                adapter.notifyDataSetChanged()
                isShowingItems = false

                btn_backButton?.visibility = View.INVISIBLE
                btn_editButton?.visibility = View.INVISIBLE
                val displayText = "Bucket List"
                bucketListTextView?.text = displayText
            }
        }
        btn_backButton?.visibility = View.INVISIBLE
        btn_editButton?.visibility = View.INVISIBLE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment bucket_list.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            bucket_list().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

