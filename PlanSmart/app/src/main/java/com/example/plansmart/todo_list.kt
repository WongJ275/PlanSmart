package com.example.plansmart

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.w3c.dom.Text
import java.lang.Integer.max
import java.util.Calendar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.math.floor

//firebase
private lateinit var auth: FirebaseAuth
private lateinit var db: FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [todo_list.newInstance] factory method to
 * create an instance of this fragment.
 */
class todo_list : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var btn_add: Button? = null
    private var btn_addItem: Button? = null
    private var btn_cancelItem: Button? = null
    private var btn_saveItem: Button? = null
    private var btn_cancelItem_: Button? = null
    private var btn_deleteItem: FloatingActionButton? = null
    private var btn_customizeItem: Button? = null
    private var btn_cancelItem__: Button? = null
    private var btn_cancelItem_customize: Button? = null

    private var editText_title: EditText? = null
    private var editText_info: EditText? = null

    private var editText_title_: EditText? = null
    private var editText_info_: EditText? = null

    private var editText_tag: EditText? = null
    private var editText_Customize: EditText? = null

    private var textView_date: TextView? = null
    private var textView_date_: TextView? = null


    private var slider_priority: Slider? = null
    private var textView_priority: TextView? = null
    private var slider_priority_: Slider? = null
    private var textView_priority_: TextView? = null

    private var layout_greyBackground: ConstraintLayout? = null
    private var layout_addItemBlock: LinearLayout? = null
    private var layout_editItemBlock: ConstraintLayout? = null
    private var layout_tagBlock: ConstraintLayout? = null
    private var layout_customizeBlock: ConstraintLayout? = null

    private var slider_showProgress: Slider? = null
    private var slider_modifyProgress: Slider? = null
    private var textView_showProgress: TextView? = null
    private var textView_modifyProgress: TextView? = null

    private var toastMessage: Toast? = null

    private var currentItemIndex = -1
    private var currentItemId = -1
    //private var itemTextView: TextView? = null

    private var btn_modifyTag: Button? = null
    private var btn_addTag: Button? = null
    private var btn_customizeAddTag: Button? = null

    private var textView_sortSelection: TextView? = null
    private var textView_sortItems: Array<TextView>? = arrayOf()
    private var selectionItemsLayout: ConstraintLayout? = null
    private var sortLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    //@SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = arrayListOf<MutableMap<String, Any>>()
        val tempList = arrayListOf<MutableMap<String, Any>>()
        val ctx: Context = view.context
        val gson = Gson()
        var c = Calendar.getInstance()
        var newItemDate: Triple<Int, Int, Int> = Triple(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        var currentSortSelection:Int = 0

        //firebase
        auth = Firebase.auth
        val db = Firebase.firestore

        // store items
        // MutableMap of {id, MutableMap of itemInfo}
        // save load this
        var itemList : MutableMap<Int, MutableMap<String, Any>>
        val filename = "todoList.json"
        /*ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write("".toByteArray())
        }
        itemList = mutableMapOf<Int, MutableMap<String, Any>>()*/
        try {
            ctx.openFileInput(filename).bufferedReader().use { inputStream ->
                itemList = gson.fromJson(inputStream, object : TypeToken<MutableMap<Int, MutableMap<String, Any>>>() {}.type)
            }
            Log.d("myTag", itemList.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            itemList = mutableMapOf<Int, MutableMap<String, Any>>()
        }




        var count = 0

        btn_add = requireView().findViewById(R.id.addBtn)
        btn_addItem = requireView().findViewById(R.id.itemAddBtn)
        btn_cancelItem = requireView().findViewById(R.id.itemCancelBtn)
        btn_saveItem = requireView().findViewById(R.id.itemSaveBtn)
        btn_cancelItem_ = requireView().findViewById(R.id.itemCancelBtn_)
        btn_deleteItem = requireView().findViewById(R.id.itemDeleteBtn)
        btn_customizeItem = requireView().findViewById(R.id.customizeBtn)
        btn_cancelItem__ = requireView().findViewById(R.id.itemCancelBtn__)
        btn_customizeItem = requireView().findViewById(R.id.customizeBtn)
        btn_modifyTag = requireView().findViewById(R.id.modifyTag)
        btn_addTag = requireView().findViewById(R.id.addTag)
        btn_cancelItem_customize = requireView().findViewById(R.id.itemCancelBtn___)
        btn_customizeAddTag = requireView().findViewById(R.id.addTagInCustomize)
        layout_greyBackground = requireView().findViewById(R.id.greyBackground)
        layout_addItemBlock = requireView().findViewById(R.id.itemAddBlock)
        layout_editItemBlock = requireView().findViewById(R.id.itemEditBlock)
        layout_tagBlock = requireView().findViewById(R.id.tagBlock)
        layout_customizeBlock = requireView().findViewById(R.id.customizeBlock)
        editText_title = requireView().findViewById(R.id.editTextTitle)
        editText_info = requireView().findViewById(R.id.editTextInfo)
        editText_Customize = requireView().findViewById(R.id.editTextcustomize)
        textView_date = requireView().findViewById(R.id.textViewDate)
        editText_title_ = requireView().findViewById(R.id.editTextTitle_)
        editText_info_ = requireView().findViewById(R.id.editTextInfo_)
        editText_tag = requireView().findViewById(R.id.editTextTag)
        textView_date_ = requireView().findViewById(R.id.textViewDate_)
        slider_priority = requireView().findViewById(R.id.prioritySlider)
        textView_priority = requireView().findViewById(R.id.priorityTextView)
        slider_priority_ = requireView().findViewById(R.id.prioritySlider_)
        textView_priority_ = requireView().findViewById(R.id.priorityTextView_)
        slider_showProgress = requireView().findViewById(R.id.progressBar)
        slider_modifyProgress = requireView().findViewById(R.id.progressBarModify)
        textView_showProgress = requireView().findViewById(R.id.progresstext)
        textView_modifyProgress = requireView().findViewById(R.id.progresstextModify)
        textView_sortSelection = requireView().findViewById(R.id.sortSelection)
        textView_sortItems = arrayOf(requireView().findViewById(R.id.sortDateAsc), requireView().findViewById(R.id.sortDateDsc),
            requireView().findViewById(R.id.sortPriorityAsc), requireView().findViewById(R.id.sortPriorityDsc),
            requireView().findViewById(R.id.sortNameAsc), requireView().findViewById(R.id.sortNameDsc))
        selectionItemsLayout = requireView().findViewById(R.id.selectionItemsLayout)
        sortLayout = requireView().findViewById(R.id.sortLayout)

        var tagList : MutableMap<Int, MutableMap<String, Any>>
        var tList = mutableListOf<Tag>()
        val tagFile = "tag.json"
        try {
            ctx.openFileInput(tagFile).bufferedReader().use { inputStream ->
                tagList = gson.fromJson(inputStream, object : TypeToken<MutableMap<Int, MutableMap<String, Any>>>() {}.type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tagList = mutableMapOf<Int, MutableMap<String, Any>>()
        }

        /*tagList = mutableMapOf<Int, MutableMap<String, Any>>()
        ctx.openFileOutput(tagFile, Context.MODE_PRIVATE).use {
            it.write("".toByteArray())
        }*/



        val adapter = SimpleAdapter(activity, list, R.layout.todo_item,
            arrayOf("Title", "Info", "Date", "DayLeft", "Level", "Tag0", "Tag1", "Tag2", "Progress", "Progress", "Id"),
            intArrayOf(R.id.title, R.id.info, R.id.date, R.id.dayLeft, R.id.level, R.id.tag0Layout, R.id.tag1Layout, R.id.tag2Layout, R.id.progressBar, R.id.progresstext, R.id.groupTextView))
        val listView: ListView = requireView().findViewById(R.id.listView)

        adapter.setViewBinder { view, data, textRepresentation ->

            if (view is TextView) {
                Log.d("tag123", view.id.toString() + " " + R.id.level)
                view.text = textRepresentation


                when (view.id) {
                    R.id.level -> {
                        view.setBackgroundColor(LevelColor(data.toString().toInt()))
                    }
                    R.id.progresstext -> {

                        Log.d("ProgressBar", data.toString().toFloat().toInt().toString())
                        view.text = data.toString().toFloat().toInt().toString()
                    }
                    R.id.groupTextView -> {
                        if (currentSortSelection == 0 || currentSortSelection == 1)
                        {
                            val m = list.indexOfFirst{it -> it["Id"].toString() == data.toString()}
                            val ts = list.map {it -> it["DayLeft"].toString().split(" ")[0].toInt()}
                            val t0 = ts.indexOf(0) //Today
                            val t1 = ts.indexOf(1) //Tomorrow
                            val t2 = ts.indexOfFirst { x -> (x in 2..7) } //Next 7days
                            val t3 = ts.indexOfFirst { x -> x>7 }// Later
                            val t4 = ts.indexOfFirst { x -> x<0}// Missed
                            view.visibility = View.VISIBLE
                            when (m) {
                                t0 -> view.text="Today"
                                t1 -> view.text="Tomorrow"
                                t2 -> view.text="Next 7days"
                                t3 -> view.text="Later"
                                t4 -> view.text="Overdue"
                                else -> view.visibility = View.GONE
                            }
                        }
                        else
                        {
                            view.visibility = View.GONE
                        }
                        Log.d("log", list.toString())
                        //view.text = data.toString()
                    }
                }
                true
            }
            else if (view is Slider) {
                when (view.id) {
                    R.id.progressBar -> {
                        view.value = data.toString().toFloat()
                    }
                }
                true
            }
            else if (view is LinearLayout) {
                when (view.id) {
                    R.id.tag0Layout, R.id.tag1Layout, R.id.tag2Layout -> {

                        val tagText = view.getChildAt(1) as TextView
                        val tagColor = view.getChildAt(0) as TextView
                        tagText.text = data.toString()

                        if (tagText.text != "") {
                            view.visibility = VISIBLE

                            var tagPos = -1
                            for (tagId in tagList.keys) {
                                if (tagList[tagId]?.get("Tag Name") == tagText.text.toString()) {
                                    tagPos = tagList[tagId]?.get("Id").toString().toFloat().toInt()
                                    break
                                }
                            }
                            val colorString = tagList[tagPos]?.get("Color").toString()
                            tagColor.setBackgroundColor(android.graphics.Color.parseColor(colorString))
                        }
                        else {
                            view.visibility = INVISIBLE
                        }
                    }
                }
                true
            }
            else {
                false
            }
        }
        listView.adapter = adapter


        var tagCount = 0
        var tagArray = mutableListOf<String>()
        var tagMap = mutableMapOf<String, MutableList<Int>>()

        var tagColorList = arrayOf("#007AFF", "#2DD36F", "#FFCC00", "#C13E34", "#AF52DE", "#6ECFF6")


        var tList2 = arrayListOf<MutableMap<String, Any>>()

        val customizeAdapter = SimpleAdapter(activity, tList2, R.layout.customize_item, arrayOf("Tag Name", "Id"), intArrayOf(R.id.tagCustomizeName, R.id.customizeToggleBtn))
        val customizeListView: ListView = requireView().findViewById(R.id.customizeListView)
        customizeAdapter.setViewBinder { view, data, textRepresentation ->

            val parentViewGroup = view.parent as? ViewGroup

            if (view is ToggleButton) {

                val id = data.toString().toFloat().toInt()
                val selectedTag = tagList[id]?.get("Tag Name").toString()

                view.isChecked = tagArray.contains(selectedTag)
                true
            }
            else if (view is TextView) {
                when (view.id) {
                    R.id.tagCustomizeName -> {
                        view.text = data as String

                        val tBtn = parentViewGroup?.findViewWithTag<ToggleButton>("toggleBtn")
                        var index = 0

                        tBtn?.setOnClickListener {

                            index = customizeListView.indexOfChild(parentViewGroup) + customizeListView.firstVisiblePosition
                            val id = tList[index].Id

                            val tagToAdd = tagList[id]?.get("Tag Name").toString()

                            if (tBtn.isChecked) {
                                if (!tagArray.contains(tagToAdd) && tagArray.size < 3) {
                                    tagArray.add(tagToAdd)
                                }
                                else {
                                    tBtn.isChecked = false
                                    Toast.makeText(this.context, "Maximum 3 tags", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                if (tagArray.contains(tagToAdd)) {
                                    tagArray.remove(tagToAdd)
                                }
                            }
                            Log.d("tagArray", tagArray.toString())
                        }

                    }
                }

                true
            }
            else {
                false
            }
        }
        customizeListView.adapter = customizeAdapter

        fun findToggleButtons(viewGroup: ViewGroup) {

            Log.d("toggle","View type: ${viewGroup.javaClass.simpleName}")
            for (i in 0 until viewGroup.childCount) {
                val childView = viewGroup.getChildAt(i)


                if (childView is ToggleButton) {
                    val tButton = childView as ToggleButton
                    tButton.isChecked = false
                }

                if (childView is ConstraintLayout) {
                    findToggleButtons(childView)
                }
            }
        }
        fun uncheckToggleButton() {
            for (i in 0 until customizeListView.childCount) {
                val listItem = customizeListView.getChildAt(i)
                if (listItem is ViewGroup) {
                    findToggleButtons(listItem)
                }
            }
            tagArray.clear()
        }

        tagArray.clear()


        val tagMapFile = "tagMap.json"
        try {
            ctx.openFileInput(tagMapFile).bufferedReader().use { inputStream ->
                tagMap = gson.fromJson(inputStream, object : TypeToken<MutableMap<String, MutableList<Int>>>() {}.type)
                Log.d("tagMap", "load: $tagMap")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tagMap = mutableMapOf<String, MutableList<Int>>()
        }


        /*ctx.openFileOutput(tagMapFile, Context.MODE_PRIVATE).use {
            it.write("".toByteArray())
        }*/


        val tagAdapter = TagAdapter(tList, list, tempList, adapter)
        val tagView: RecyclerView = requireView().findViewById(R.id.tagView)
        tagView.adapter = tagAdapter
        val mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tagView.layoutManager = mLayoutManager



        val tagModifyAdapter = SimpleAdapter(activity, tList2, R.layout.tag_modify_item, arrayOf("Tag Name"), intArrayOf(R.id.tagModifyName))
        val tagListView: ListView = requireView().findViewById(R.id.tagListView)
        tagModifyAdapter.setViewBinder { view, data, textRepresentation ->

            val parentViewGroup = view.parent as? ViewGroup
            if (view is TextView) {

                view.text = data as String

                val faBtn = parentViewGroup?.findViewWithTag<FloatingActionButton>("floatingActionButton")

                faBtn?.setOnClickListener {

                    val index = tagListView.indexOfChild(parentViewGroup) + tagListView.firstVisiblePosition
                    Log.d("tagDebug", index.toString())
                    val id = tList[index].Id

                    Log.d("tagMap", tList.toString())
                    Log.d("tagMap", tList2.toString())


                    val tname = tList[index].name
                    val idsToRemove = mutableListOf<Int>()

                    if (tagMap.contains(tname)) {
                        for (ids in tagMap[tname]!!) {

                            for (entry in list) {
                                if (entry["Id"] == ids) {
                                    if (entry.get("Tag0") == tname) {
                                        entry["Tag0"] = entry["Tag1"].toString()
                                        entry["Tag1"] = entry["Tag2"].toString()
                                        entry["Tag2"] = ""
                                    }
                                    else if (entry.get("Tag1") == tname) {
                                        entry["Tag1"] = entry["Tag2"].toString()
                                        entry["Tag2"] = ""
                                    }
                                    else if (entry.get("Tag2") == tname) {
                                        entry["Tag2"] = ""
                                    }
                                }
                            }

                            for (entry in tempList) {
                                if (entry["Id"] == ids) {
                                    if (entry.get("Tag0") == tname) {
                                        entry["Tag0"] = entry["Tag1"].toString()
                                        entry["Tag1"] = entry["Tag2"].toString()
                                        entry["Tag2"] = ""
                                    }
                                    else if (entry.get("Tag1") == tname) {
                                        entry["Tag1"] = entry["Tag2"].toString()
                                        entry["Tag2"] = ""
                                    }
                                    else if (entry.get("Tag2") == tname) {
                                        entry["Tag2"] = ""
                                    }
                                }
                            }

                            idsToRemove.add(ids)
                        }
                    }

                    for (ids in idsToRemove) {
                        tagMap[tname]?.remove(ids)
                        if (tagMap[tname].isNullOrEmpty()) {
                            tagMap.remove(tname)
                        }
                    }

                    //Log.d("tagMap", tagMap.toString())
                    //Log.d("tagMap", itemList.toString())

                    if (tagAdapter.chosenPosition == index) {
                        list.clear()
                        for (task in tempList) {
                            list.add(task)
                        }
                        tagAdapter.chosenPosition = -1
                    }

                    tList.removeAt(index)
                    tList2.removeAt(index)
                    tagList[id]?.set("Chosen", false)
                    tagList.remove(id)

                    Log.d("tagDebug", tList.toString())
                    Log.d("tagDebug", tList2.toString())
                    Log.d("tagDebug", tagList.toString())

                    adapter.notifyDataSetChanged()
                    tagAdapter.notifyDataSetChanged()
                    tagModifyAdapter.notifyDataSetChanged()
                    customizeAdapter.notifyDataSetChanged()

                    val map: MutableMap<String, Any> = HashMap()

                    for (item in tempList) {
                        val id1 = item["Id"].toString().toFloat().toInt()

                        map["Title"] = item["Title"]!!
                        map["Info"] = item["Info"]!!
                        map["Id"] = item["Id"]!!
                        map["Date"] = gson.toJson(newItemDate)
                        map["Level"] = item["Level"]!!
                        map["Progress"] = item["Progress"]!!

                        map["Tag0"] = item["Tag0"]!!
                        map["Tag1"] = item["Tag1"]!!
                        map["Tag2"] = item["Tag2"]!!

                        itemList[id1] = HashMap(map)
                    }

                    val listJson = gson.toJson(itemList)
                    ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                        it.write(listJson.toByteArray())
                    }

                    val tagJson = gson.toJson(tagList)
                    ctx.openFileOutput(tagFile, Context.MODE_PRIVATE).use {
                        it.write(tagJson.toByteArray())
                    }

                    val mapJson = gson.toJson(tagMap)
                    ctx.openFileOutput(tagMapFile, Context.MODE_PRIVATE).use {
                        it.write(mapJson.toByteArray())
                    }

                    val usr = auth.currentUser
                    if (usr != null && !usr.isAnonymous) {
                        Log.d("log", "signed in")
                        val docRef = db.collection("data").document(usr.uid)
                        docRef.update(hashMapOf(filename.split(".")[0] to listJson, tagFile.split(".")[0] to tagJson, tagMapFile.split(".")[0] to mapJson) as Map<String, Any>)
                    }

                }
                true
            } else {
                false
            }
        }
        tagListView.adapter = tagModifyAdapter


        for (k in tagList.keys) {

            val tag_ = Tag(tagList[k]?.get("Tag Name").toString(), false, tagList[k]?.get("Id").toString().toFloat().toInt(), tagList[k]?.get("Color").toString())
            tList.add(tag_)

            val map: MutableMap<String, Any> = HashMap()
            map["Tag Name"] = tagList[k]?.get("Tag Name").toString()
            map["Chosen"] = tagList[k]?.get("Chosen").toString()
            map["Id"] = tagList[k]?.get("Id").toString()
            map["Color"] = tagList[k]?.get("Color").toString()
            tList2.add(map)

            tagCount = max(tagList[k]?.get("Id").toString().toFloat().toInt() + 1, tagCount)

        }
        tagAdapter.notifyDataSetChanged()
        tagModifyAdapter.notifyDataSetChanged()
        customizeAdapter.notifyDataSetChanged()



        for (key in itemList.keys) {
            //list.add(itemLists[key]!!.first)
            val map: MutableMap<String, Any> = HashMap()
            map["Title"] = itemList[key]?.get("Title").toString()
            map["Info"] = itemList[key]?.get("Info").toString()
            map["Id"] = itemList[key]?.get("Id").toString().toFloat().toInt()
            val d: Triple<Int,Int,Int> = gson.fromJson(itemList[key]?.get("Date").toString(), object : TypeToken<Triple<Int,Int,Int>>() {}.type )
            map["Date"] = d.first.toString() + " - " + (d.second + 1).toString() + " - " + d.third.toString()
            val date: Calendar = Calendar.getInstance()
            date.set(d.first, d.second, d.third, 0, 0, 0)
            map["DayLeft"] = ((floor((date.timeInMillis - c.timeInMillis + 86400000) / 86400000F)).toInt()).toString() + " days"
            map["Level"] = itemList[key]?.get("Level").toString()
            map["Progress"] = itemList[key]?.get("Progress").toString().toFloat().toInt()

            map["Tag0"] = itemList[key]?.get("Tag0").toString()
            map["Tag1"] = itemList[key]?.get("Tag1").toString()
            map["Tag2"] = itemList[key]?.get("Tag2").toString()


            count = max(itemList[key]?.get("Id").toString().toFloat().toInt() + 1, count)
            list.add(map)
            tempList.add(map)
            Log.d("myTag", itemList[key]!!.toString())
        }
        adapter.notifyDataSetChanged()
        sortList(list, currentSortSelection, adapter)

        listView.setOnItemClickListener { adapterV, v, i, l ->
            layout_greyBackground!!.visibility = View.VISIBLE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.VISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE

            val item: HashMap<*, *>? = adapterV.getItemAtPosition(i) as? HashMap<*, *>
            val id: Int = item?.get("Id") as Int
            currentItemId = id
            currentItemIndex = i


            editText_title_!!.setText(itemList[id]?.get("Title").toString())
            editText_info_!!.setText(itemList[id]?.get("Info").toString())
            val d: Triple<Int,Int,Int> = gson.fromJson(itemList[id]?.get("Date").toString(), object : TypeToken<Triple<Int,Int,Int>>() {}.type )
            newItemDate = d
            textView_date_!!.text = d.first.toString() + " - " + (d.second + 1).toString() + " - " + d.third.toString()

            slider_priority_!!.value = itemList[id]?.get("Level").toString().toInt().toFloat()
            slider_modifyProgress!!.value = itemList[id]?.get("Progress").toString().toFloat()
        }

        btn_addItem!!.setOnClickListener {
            val map: MutableMap<String, Any> = HashMap()

            val t: String = editText_title!!.text.toString()
            if (t!="")
            {
                map["Title"] = t
                map["Info"] = editText_info!!.text.toString()
                map["Id"] = count
                map["Date"] = gson.toJson(newItemDate)
                map["Level"] = slider_priority!!.value.toInt().toString()
                map["Progress"] = "0.0"

                map["Tag0"] = tagArray.getOrNull(0) ?: ""
                map["Tag1"] = tagArray.getOrNull(1) ?: ""
                map["Tag2"] = tagArray.getOrNull(2) ?: ""

                for (i in 0 until tagArray.size) {
                    if (tagMap.contains(tagArray[i])) {
                        tagMap[tagArray[i]]?.add(count)
                    }
                    else {
                        tagMap[tagArray[i]] = mutableListOf<Int>()
                        tagMap[tagArray[i]]?.add(count)
                    }
                }

                Log.d("tagMap", tagMap.toString())
                val json = gson.toJson(tagMap)

                ctx.openFileOutput(tagMapFile, Context.MODE_PRIVATE).use {
                    it.write(json.toByteArray())
                }
                val usr = auth.currentUser
                if (usr != null && !usr.isAnonymous) {
                    Log.d("log", "signed in")
                    val docRef = db.collection("data").document(usr.uid)
                    docRef.update(tagMapFile.split(".")[0], json)
                }


                uncheckToggleButton()


                itemList[count] = HashMap(map)

                Log.d("itemList", itemList.toString())

                map["Date"] = newItemDate.first.toString() + " - " + (newItemDate.second + 1).toString() + " - " + newItemDate.third.toString()
                val date: Calendar = Calendar.getInstance()
                date.set(newItemDate.first, newItemDate.second, newItemDate.third, 0, 0, 0)
                map["DayLeft"] = (floor((date.timeInMillis - c.timeInMillis + 86400000) / 86400000F).toInt()).toString() + " days"
                list.add(map)
                tempList.add(map)
                count++


                if (tagAdapter.chosenPosition != -1) {
                    list.clear()

                    val chosenTag = tList[tagAdapter.chosenPosition].name
                    for (task in tempList) {
                        if (task["Tag0"] == chosenTag || task["Tag1"] == chosenTag || task["Tag2"] == chosenTag ) {
                            list.add(task)
                        }
                    }
                }
                else if (tagAdapter.chosenPosition == -1) {
                    list.clear()

                    for (task in tempList) {
                        list.add(task)
                    }
                }
            }

            /*if (!isShowingItems)
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
            }
            else
            {
                map["Title"] = "TestItemTitle"
                map["Info"] = "TestInfo"
                list.add(map)
                itemLists[currentBucketListId]?.second?.add(map)
            }*/
            adapter.notifyDataSetChanged()
            sortList(list, currentSortSelection, adapter)

            val json = gson.toJson(itemList)

            ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(filename.split(".")[0], json)
            }

            Log.d("myTag", itemList.toString())
            //Log.d("myTag", json)
            layout_greyBackground!!.visibility = View.GONE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE
        }

        btn_add!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.VISIBLE
            layout_addItemBlock!!.visibility = View.VISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE

            editText_title!!.setText("")
            editText_info!!.setText("")

            c = Calendar.getInstance()
            newItemDate = Triple(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            textView_date!!.text = newItemDate.first.toString() + " - " + (newItemDate.second + 1) + " - " + newItemDate.third
        }

        textView_date!!.setOnClickListener{
            c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                ctx,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our text view.
                    Log.d("myTag", (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year))
                    newItemDate = Triple(year, monthOfYear, dayOfMonth)
                    textView_date!!.text = year.toString() + " - " + (monthOfYear + 1) + " - " + dayOfMonth.toString()
                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        btn_cancelItem!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE

            uncheckToggleButton()
        }


        btn_customizeItem!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.VISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.VISIBLE
        }


        btn_cancelItem_customize!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.VISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_addItemBlock!!.visibility = View.VISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE
        }


        btn_modifyTag!!.setOnClickListener {

            editText_tag!!.setText("")

            layout_greyBackground!!.visibility = View.VISIBLE
            layout_tagBlock!!.visibility = View.VISIBLE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE
        }


        btn_addTag!!.setOnClickListener {
            val map: MutableMap<String, Any> = HashMap()

            val t: String = editText_tag!!.text.toString()
            if (t!="") {

                var allow = true
                for (tagg in tList) {
                    if (tagg.name == t) {
                        allow = false
                    }
                }

                if (allow) {
                    tList.add(Tag(t, false, tagCount, tagColorList[tagCount % tagColorList.size]))

                    map["Tag Name"] = t
                    map["Chosen"] = false
                    map["Id"] = tagCount
                    map["Color"] = tagColorList[tagCount % tagColorList.size]
                    tList2.add(map)


                    tagList[tagCount] = HashMap(map)

                    tagCount++
                }
                else {
                    Toast.makeText(this.context, "Tag already exists", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this.context, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
            editText_tag!!.setText("")

            tagModifyAdapter.notifyDataSetChanged()
            tagAdapter.notifyDataSetChanged()
            customizeAdapter.notifyDataSetChanged()

            val tagJson = gson.toJson(tagList)
            ctx.openFileOutput(tagFile, Context.MODE_PRIVATE).use {
                it.write(tagJson.toByteArray())
            }
            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(tagFile.split(".")[0], tagJson)
            }

        }


        btn_customizeAddTag!!.setOnClickListener {
            val map: MutableMap<String, Any> = HashMap()

            val t: String = editText_Customize!!.text.toString()
            if (t!="") {

                var allow = true
                for (tagg in tList) {
                    if (tagg.name == t) {
                        allow = false
                    }
                }

                if (allow) {
                    tList.add(Tag(t, false, tagCount, tagColorList[tagCount % tagColorList.size]))

                    map["Tag Name"] = t
                    map["Chosen"] = false
                    map["Id"] = tagCount
                    map["Color"] = tagColorList[tagCount % tagColorList.size]
                    tList2.add(map)


                    tagList[tagCount] = HashMap(map)

                    tagCount++
                }
                else {
                    Toast.makeText(this.context, "Tag already exists", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this.context, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
            editText_Customize!!.setText("")

            tagModifyAdapter.notifyDataSetChanged()
            tagAdapter.notifyDataSetChanged()
            customizeAdapter.notifyDataSetChanged()

            val tagJson = gson.toJson(tagList)
            ctx.openFileOutput(tagFile, Context.MODE_PRIVATE).use {
                it.write(tagJson.toByteArray())
            }
            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(tagFile.split(".")[0], tagJson)
            }

        }


        btn_cancelItem__!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE
        }


        layout_addItemBlock!!.setOnClickListener {
            
        }

        btn_saveItem!!.setOnClickListener {
            val map: MutableMap<String, Any> = HashMap()

            val t: String = editText_title_!!.text.toString()
            if (t!="")
            {
                map["Title"] = t
                map["Info"] = editText_info_!!.text.toString()
                map["Id"] = currentItemId
                map["Date"] = gson.toJson(newItemDate)
                map["Level"] = slider_priority_!!.value.toInt().toString()
                map["Progress"] = slider_modifyProgress!!.value.toInt().toString()

                map["Tag0"] = list[currentItemIndex]["Tag0"].toString()
                map["Tag1"] = list[currentItemIndex]["Tag1"].toString()
                map["Tag2"] = list[currentItemIndex]["Tag2"].toString()

                itemList[currentItemId] = HashMap(map)

                map["Date"] = newItemDate.first.toString() + " - " + (newItemDate.second + 1).toString() + " - " + newItemDate.third.toString()
                val date: Calendar = Calendar.getInstance()
                date.set(newItemDate.first, newItemDate.second, newItemDate.third, 0, 0, 0)
                map["DayLeft"] = (floor((date.timeInMillis - c.timeInMillis + 86400000) / 86400000F).toInt()).toString() + " days"

                var posToChange = -1
                for (i in 0 until tempList.size) {
                    if (currentItemId == tempList[i]["Id"]) {
                        posToChange = i
                        break
                    }
                }
                tempList[posToChange] = map

                if (tagAdapter.chosenPosition != -1) {
                    list.clear()

                    val chosenTag = tList[tagAdapter.chosenPosition].name
                    for (task in tempList) {
                        if (task["Tag0"] == chosenTag || task["Tag1"] == chosenTag || task["Tag2"] == chosenTag ) {
                            list.add(task)
                        }
                    }
                }
                else if (tagAdapter.chosenPosition == -1) {
                    list.clear()

                    for (task in tempList) {
                        list.add(task)
                    }
                }
            }
            adapter.notifyDataSetChanged()
            sortList(list, currentSortSelection, adapter)

            val json = gson.toJson(itemList)

            ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(filename.split(".")[0], json)
            }

            Log.d("myTag", itemList.toString())
            //Log.d("myTag", json)
            layout_greyBackground!!.visibility = View.GONE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE
        }

        textView_date_!!.setOnClickListener{
            // on below line we are getting
            // our day, month and year.
            val d: Triple<Int,Int,Int> = gson.fromJson(itemList[currentItemId]?.get("Date").toString(), object : TypeToken<Triple<Int,Int,Int>>() {}.type )
            val year = d.first
            val month = d.second
            val day = d.third

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                ctx,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our text view.
                    Log.d("myTag", (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year))
                    newItemDate = Triple(year, monthOfYear, dayOfMonth)
                    textView_date_!!.text = year.toString() + " - " + (monthOfYear + 1) + " - " + dayOfMonth.toString()
                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.datePicker.updateDate(year, month, day)
            datePickerDialog.show()
        }

        btn_cancelItem_!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE
        }

        btn_deleteItem!!.setOnClickListener {

            for (i in 0 until 3) {
                val tagName = list[currentItemIndex]["Tag$i"]
                if (tagName != "") {
                    tagMap[tagName]?.remove(currentItemId)
                    if (tagMap[tagName].isNullOrEmpty()) {
                        tagMap.remove(tagName)
                    }
                }
            }

            Log.d("tagMap", tagMap.toString())

            val tjson = gson.toJson(tagMap)

            ctx.openFileOutput(tagMapFile, Context.MODE_PRIVATE).use {
                it.write(tjson.toByteArray())
            }
            val usr = auth.currentUser
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(tagMapFile.split(".")[0], tjson)
            }

            var posToRemove = -1
            for (i in 0 until tempList.size) {
                if (tempList[i]["Id"] == currentItemId) {
                    posToRemove = i
                }
            }

            tempList.removeAt(posToRemove)
            list.removeAt(currentItemIndex)

            itemList.remove(currentItemId)

            layout_greyBackground!!.visibility = View.GONE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE

            adapter.notifyDataSetChanged()
            sortList(list, currentSortSelection, adapter)
            val json = gson.toJson(itemList)

            ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            if (usr != null && !usr.isAnonymous) {
                Log.d("log", "signed in")
                val docRef = db.collection("data").document(usr.uid)
                docRef.update(filename.split(".")[0], json)
            }
        }

        layout_editItemBlock!!.setOnClickListener {

        }

        layout_greyBackground!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_tagBlock!!.visibility = View.INVISIBLE
            layout_customizeBlock!!.visibility = View.INVISIBLE

            uncheckToggleButton()
        }

        slider_priority!!.addOnChangeListener { slider: Slider, fl: Float, b: Boolean ->
            textView_priority?.text = fl.toInt().toString()
        }
        slider_priority_!!.addOnChangeListener { slider: Slider, fl: Float, b: Boolean ->
            textView_priority_?.text = fl.toInt().toString()
        }
        slider_modifyProgress!!.addOnChangeListener { slider: Slider, fl: Float, b: Boolean ->
            textView_modifyProgress?.text = fl.toInt().toString()
        }

        sortLayout!!.setOnClickListener {
            textView_sortSelection!!.performClick()
        }

        textView_sortSelection!!.setOnClickListener {
            selectionItemsLayout?.visibility = if (selectionItemsLayout?.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        for (i in textView_sortItems!!.indices)
        {
            textView_sortItems!![i].setOnClickListener {
                selectionItemsLayout?.visibility = View.GONE
                textView_sortSelection?.text = textView_sortItems!![i].text
                currentSortSelection = i
                tagAdapter.currentSortSelection = currentSortSelection
                sortList(list, currentSortSelection, adapter)
            }
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

    fun LevelColor(level: Int): Int {
        val col: Color = if (level >= 5) Color(200, 80 + 30 * (10 - level), 50, 50) else Color(80 + 30 * (level - 1), 200, 50, 10 + 10 * level)
        return col.toArgb()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment todo_list.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            todo_list().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}