package com.example.plansmart

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
 * Use the [daily_goals.newInstance] factory method to
 * create an instance of this fragment.
 */
class daily_goals : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var btn_add: Button? = null
    private var btn_addItem: Button? = null
    private var btn_cancelItem: Button? = null
    private var btn_saveItem: Button? = null
    private var btn_cancelItem_: Button? = null
    private var btn_deleteItem: FloatingActionButton? = null

    private var editText_title: EditText? = null
    private var editText_info: EditText? = null

    private var editText_title_: EditText? = null
    private var editText_info_: EditText? = null

    private var editText_number: EditText? = null
    private var spinner_date: Spinner? = null
    private var editText_number_: EditText? = null
    private var spinner_date_: Spinner? = null

    private var textView_date_: TextView? = null

    private var slider_priority: Slider? = null
    private var textView_priority: TextView? = null

    private var slider_priority_: Slider? = null
    private var textView_priority_: TextView? = null

    private var slider_showProgress: Slider? = null
    private var slider_modifyProgress: Slider? = null
    private var textView_showProgress: TextView? = null
    private var textView_modifyProgress: TextView? = null

    private var layout_greyBackground: ConstraintLayout? = null
    private var layout_addItemBlock: LinearLayout? = null
    private var layout_editItemBlock: ConstraintLayout? = null

    private var toastMessage: Toast? = null

    private var currentItemIndex = -1
    private var currentItemId = -1
    //private var itemTextView: TextView? = null

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
        return inflater.inflate(R.layout.fragment_daily_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = arrayListOf<MutableMap<String, Any>>()
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
        val filename = "dailyGoals.json"
        /*ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write("".toByteArray())
        }
        itemList = mutableMapOf<Int, MutableMap<String, Any>>()*/
        try {
            ctx.openFileInput("dailyGoals.json").bufferedReader().use { inputStream ->
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
        layout_greyBackground = requireView().findViewById(R.id.greyBackground)
        layout_addItemBlock = requireView().findViewById(R.id.itemAddBlock)
        layout_editItemBlock = requireView().findViewById(R.id.itemEditBlock)
        editText_title = requireView().findViewById(R.id.editTextTitle)
        editText_info = requireView().findViewById(R.id.editTextInfo)
        editText_number = requireView().findViewById(R.id.editTextNumber)
        spinner_date = requireView().findViewById(R.id.spinnerDate)
        editText_number_ = requireView().findViewById(R.id.editTextNumber_)
        spinner_date_ = requireView().findViewById(R.id.spinnerDate_)
        editText_title_ = requireView().findViewById(R.id.editTextTitle_)
        editText_info_ = requireView().findViewById(R.id.editTextInfo_)
        textView_date_ = requireView().findViewById(R.id.textViewDate_)
        slider_priority = requireView().findViewById(R.id.prioritySlider)
        textView_priority = requireView().findViewById(R.id.priorityTextView)
        slider_priority_ = requireView().findViewById(R.id.prioritySlider_)
        textView_priority_ = requireView().findViewById(R.id.priorityTextView_)
        slider_showProgress = requireView().findViewById(R.id.progressBarGoal)
        slider_modifyProgress = requireView().findViewById(R.id.progressBarModifyGoal)
        textView_showProgress = requireView().findViewById(R.id.progresstext)
        textView_modifyProgress = requireView().findViewById(R.id.progresstextModify)

        textView_sortSelection = requireView().findViewById(R.id.sortSelection)
        textView_sortItems = arrayOf(requireView().findViewById(R.id.sortDateAsc), requireView().findViewById(R.id.sortDateDsc),
            requireView().findViewById(R.id.sortPriorityAsc), requireView().findViewById(R.id.sortPriorityDsc),
            requireView().findViewById(R.id.sortNameAsc), requireView().findViewById(R.id.sortNameDsc))
        selectionItemsLayout = requireView().findViewById(R.id.selectionItemsLayout)
        sortLayout = requireView().findViewById(R.id.sortLayout)

        val arraySpinner = arrayOf(
            "Day", "Week", "Month"
        )
        val a: ArrayAdapter<String> = ArrayAdapter<String>(
            ctx,
            android.R.layout.simple_spinner_item,
            arraySpinner
        )
        a.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_date!!.adapter = a
        val a_: ArrayAdapter<String> = ArrayAdapter<String>(
            ctx,
            android.R.layout.simple_spinner_item,
            arraySpinner
        )
        a_.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_date_!!.adapter = a_


        val adapter = SimpleAdapter(activity, list, R.layout.goal,
            arrayOf("Title", "Info", "RepeatType", "TimeLeft", "Level", "Progress", "Progress", "Id"), intArrayOf(R.id.title, R.id.info, R.id.repeatType, R.id.timeLeft, R.id.level, R.id.progressBarGoal, R.id.progresstext, R.id.groupTextView))
        adapter.setViewBinder { view, data, textRepresentation ->
            if (view is TextView) {
                view.text = textRepresentation
                when (view.id) {
                    R.id.level -> {
                        view.setBackgroundColor(LevelColor(data.toString().toInt()))
                    }
                    R.id.progresstext -> {
                        view.text = data.toString().toFloat().toInt().toString()
                    }
                    R.id.groupTextView -> {
                        if (currentSortSelection == 0 || currentSortSelection == 1)
                        {
                            val m = list.indexOfFirst{it -> it["Id"].toString() == data.toString()}
                            val ts = list.map {it -> if (it["TimeLeft"].toString().split(" ")[1] == "hours") 0 else it["TimeLeft"].toString().split(" ")[0].toInt()}
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
                    R.id.progressBarGoal -> {
                        view.value = data.toString().toFloat()
                    }
                }
                true
            }
            else {
                false
            }
        }
        val listView: ListView = requireView().findViewById(R.id.listView)
        listView.adapter = adapter


        for (key in itemList.keys) {
            Log.d("log123", itemList[key].toString())
            //list.add(itemLists[key]!!.first)
            val map: MutableMap<String, Any> = HashMap()
            map["Title"] = itemList[key]?.get("Title").toString()
            map["Info"] = itemList[key]?.get("Info").toString()
            map["Id"] = itemList[key]?.get("Id").toString().toFloat().toInt()
            var d: Triple<Int,Int,Int> = gson.fromJson(itemList[key]?.get("Date").toString(), object : TypeToken<Triple<Int, Int, Int>>() {}.type )
            val r: Pair<Int,String> = gson.fromJson(itemList[key]?.get("RepeatType").toString(), object : TypeToken<Pair<Int,String>>() {}.type )
            map["RepeatType"] = r.first.toString() + " " + r.second
            val date: Calendar = Calendar.getInstance()
            date.set(d.first, d.second, d.third, 0, 0, 0)
            var record: MutableList<Triple<Triple<Int, Int, Int>, Triple<Int, Int, Int>, Float>> = gson.fromJson(itemList[key]?.get("Record").toString(), object : TypeToken<MutableList<Triple<Triple<Int, Int, Int>, Triple<Int, Int, Int>, Float>>>() {}.type )
            var progress: Float = 1F
            when (r.second){
                "Day" -> {
                    date.add(Calendar.DATE, r.first)
                    while (date.timeInMillis < c.timeInMillis)
                    {
                        date.add(Calendar.DATE, -1 * r.first)
                        val e: Triple<Int, Int, Int> = Triple(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                        date.add(Calendar.DATE, r.first)
                        d = Triple(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                        record.add(Triple(e, d, progress))
                        progress = 0F
                        date.add(Calendar.DATE, r.first)
                    }
                    date.add(Calendar.DATE, -1 * r.first)
                }
                "Week" -> {
                    date.add(Calendar.DATE, r.first * 7)
                    while (date.timeInMillis < c.timeInMillis)
                    {
                        date.add(Calendar.DATE, -7 * r.first)
                        val e: Triple<Int, Int, Int> = Triple(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                        date.add(Calendar.DATE, r.first * 7)
                        d = Triple(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                        record.add(Triple(e, d, progress))
                        progress = 0F
                        date.add(Calendar.DATE, r.first * 7)
                    }
                    date.add(Calendar.DATE, -7 * r.first)
                }
                "Month" -> {
                    date.add(Calendar.MONTH, r.first)
                    while (date.timeInMillis < c.timeInMillis)
                    {
                        date.add(Calendar.MONTH, -1 * r.first)
                        val e: Triple<Int, Int, Int> = Triple(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                        date.add(Calendar.MONTH, r.first)
                        d = Triple(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
                        record.add(Triple(e, d, progress))
                        progress = 0F
                        date.add(Calendar.MONTH, r.first)
                    }
                    date.add(Calendar.MONTH, -1 * r.first)
                }
            }
            itemList[key]?.set("Date", gson.toJson(Triple<Int, Int, Int>(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE))))
            itemList[key]?.set("Record", gson.toJson(record))
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

            when (r.second){
                "Day" -> {date.add(Calendar.DATE, r.first)}
                "Week" -> {date.add(Calendar.DATE, r.first * 7)}
                "Month" -> {date.add(Calendar.MONTH, r.first)}
            }
            Log.d("tag123", ((date.timeInMillis - c.timeInMillis)).toString())
            map["TimeLeft"] = if ((floor((date.timeInMillis - c.timeInMillis) / 86400000F)).toLong() != 0.toLong()) (floor(((date.timeInMillis - c.timeInMillis) / 86400000F)).toInt().toString() + " days ") else ((floor(((date.timeInMillis - c.timeInMillis) % 86400000)/3600000F)).toInt().toString() + " hours")
            map["Level"] = itemList[key]?.get("Level").toString()
            map["Progress"] = itemList[key]?.get("Progress").toString().toFloat().toInt()

            count = max(itemList[key]?.get("Id").toString().toFloat().toInt() + 1, count)
            list.add(map)
            Log.d("myTag", itemList[key]!!.toString())


        }
        sortList(list, currentSortSelection, adapter)
        adapter.notifyDataSetChanged()

        listView.setOnItemClickListener { adapterV, v, i, l ->
            layout_greyBackground!!.visibility = View.VISIBLE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.VISIBLE

            val item: HashMap<*, *>? = adapterV.getItemAtPosition(i) as? HashMap<*, *>
            val id: Int = item?.get("Id") as Int
            currentItemId = id
            currentItemIndex = i

            editText_title_!!.setText(itemList[id]?.get("Title").toString())
            editText_info_!!.setText(itemList[id]?.get("Info").toString())
            //val d: Triple<Int,Int,Int> = gson.fromJson(itemList[id]?.get("Date").toString(), object : TypeToken<Triple<Int, Int, Int>>() {}.type )
            //newItemDate = d
            slider_priority_!!.value = itemList[id]?.get("Level").toString().toInt().toFloat()
            slider_modifyProgress!!.value = itemList[id]?.get("Progress").toString().toFloat()
            val r: Pair<Int,String> = gson.fromJson(itemList[id]?.get("RepeatType").toString(), object : TypeToken<Pair<Int,String>>() {}.type )
            editText_number_!!.setText(r.first.toString())

            spinner_date_!!.setSelection(when (r.second) {"Day" -> {0} "Week" -> {1} "Month" -> {2} else -> 0} )
        }

        btn_addItem!!.setOnClickListener {
            val map: MutableMap<String, Any> = HashMap()

            val t: String = editText_title!!.text.toString()
            if (t!="")
            {
                map["Title"] = t
                map["Info"] = editText_info!!.text.toString()
                map["Id"] = count
                map["Date"] = gson.toJson(Triple(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)))

                map["Level"] = slider_priority!!.value.toInt().toString()
                map["Progress"] = "0.0"
                val num: Int = if (editText_number!!.text.toString()=="") 1 else max(editText_number!!.text.toString().toInt(), 1)
                map["RepeatType"] = gson.toJson(Pair<Int, String>(num, spinner_date!!.selectedItem.toString()))
                map["Record"] = mutableListOf<MutableList<Triple<Triple<Int, Int, Int>, Triple<Int, Int, Int>, Float>>>()
                itemList[count] = HashMap(map)

                val date: Calendar = Calendar.getInstance()
                date.set(newItemDate.first, newItemDate.second, newItemDate.third, 0, 0, 0)
                when (spinner_date!!.selectedItem.toString()){
                    "Day" -> { date.add(Calendar.DATE, num) }
                    "Week" -> { date.add(Calendar.DATE, num * 7) }
                    "Month" -> { date.add(Calendar.MONTH, num) }
                }
                map["TimeLeft"] = if (((date.timeInMillis - c.timeInMillis) / 86400000)!= 0.toLong()) (((date.timeInMillis - c.timeInMillis) / 86400000).toString() + " days ") else ((((date.timeInMillis - c.timeInMillis) % 86400000)/3600000).toString() + " hours")
                map["RepeatType"] = num.toString() + " " + spinner_date!!.selectedItem.toString()
                list.add(map)
                Log.d("ttt", map.toString())
                count++
            }
            sortList(list, currentSortSelection, adapter)
            adapter.notifyDataSetChanged()


            val json = gson.toJson(itemList)

            val filename = "dailyGoals.json"
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
        }

        btn_add!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.VISIBLE
            layout_addItemBlock!!.visibility = View.VISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE

            editText_title!!.setText("")
            editText_info!!.setText("")
            editText_number!!.setText("1")
            spinner_date!!.setSelection(0)

            c = Calendar.getInstance()
            newItemDate = Triple(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            //editText_date!!.text = newItemDate.first.toString() + " - " + (newItemDate.second + 1) + " - " + newItemDate.third
        }

        /*textView_date!!.setOnClickListener{
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
        }*/

        btn_cancelItem!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
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
                map["Date"] = gson.toJson(Triple(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)))
                map["Level"] = slider_priority_!!.value.toInt().toString()
                map["Progress"] = slider_modifyProgress!!.value.toInt().toString()

                val num: Int = if (editText_number_!!.text.toString()=="") 1 else max(editText_number_!!.text.toString().toInt(), 1)
                map["RepeatType"] = gson.toJson(Pair<Int, String>(num, spinner_date_!!.selectedItem.toString()))
                map["Record"] = itemList.get(currentItemId)?.get("Record").toString()
                itemList[currentItemId] = HashMap(map)

                val date: Calendar = Calendar.getInstance()
                date.set(newItemDate.first, newItemDate.second, newItemDate.third, 0, 0, 0)
                when (spinner_date_!!.selectedItem.toString()){
                    "Day" -> { date.add(Calendar.DATE, num) }
                    "Week" -> { date.add(Calendar.DATE, num * 7) }
                    "Month" -> { date.add(Calendar.MONTH, num) }
                }

                map["RepeatType"] = num.toString() + " " + spinner_date_!!.selectedItem.toString()
                map["TimeLeft"] = if (((date.timeInMillis - c.timeInMillis) / 86400000)!= 0.toLong()) (((date.timeInMillis - c.timeInMillis) / 86400000).toString() + " days ") else ((((date.timeInMillis - c.timeInMillis) % 86400000)/3600000).toString() + " hours")
                list[currentItemIndex] = map
            }
            sortList(list, currentSortSelection, adapter)
            adapter.notifyDataSetChanged()


            val json = gson.toJson(itemList)

            val filename = "dailyGoals.json"
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
        }

        /*textView_date_!!.setOnClickListener{
            // on below line we are getting
            // our day, month and year.
            val d: Triple<Int,Int,Int> = gson.fromJson(itemList[currentItemId]?.get("Date").toString(), object : TypeToken<Triple<Int, Int, Int>>() {}.type )
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
        }*/

        btn_cancelItem_!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
        }

        btn_deleteItem!!.setOnClickListener {
            list.removeAt(currentItemIndex)
            itemList.remove(currentItemId)

            layout_greyBackground!!.visibility = View.GONE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
            sortList(list, currentSortSelection, adapter)
            adapter.notifyDataSetChanged()

            val json = gson.toJson(itemList)

            val filename = "dailyGoals.json"
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

        layout_editItemBlock!!.setOnClickListener {

        }

        layout_greyBackground!!.setOnClickListener {
            layout_greyBackground!!.visibility = View.GONE
            layout_addItemBlock!!.visibility = View.INVISIBLE
            layout_editItemBlock!!.visibility = View.INVISIBLE
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
        val t1 = (m1["TimeLeft"] as String).split(" ")
        val t2 = (m2["TimeLeft"] as String).split(" ")
        when {
            (t1[1]==t2[1]) -> t1[0].toInt().compareTo(t2[0].toInt())
            else -> t2[1].length.compareTo(t1[1].length)
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
         * @return A new instance of fragment daily_goals.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            daily_goals().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}