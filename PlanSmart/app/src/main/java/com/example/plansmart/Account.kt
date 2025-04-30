package com.example.plansmart

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var textViewUsername: TextView? = null

    private var editTextUsername: EditText? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null

    private var divider: View? = null

    private var SigninBtn: Button? = null
    private var CreateAccountBtn: Button? = null
    private var RegisterBtn: Button? = null
    private var LogoutBtn: Button? = null
    private var BackBtn: Button? = null

    private var textViewMessage: TextView? = null

    private var fileNameList: ArrayList<String> = arrayListOf("bucketList.json", "dailyGoals.json", "todoList.json", "tag.json", "tagMap.json", "username.txt")

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
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ctx: Context = view.context

        val auth = Firebase.auth
        val db = Firebase.firestore

        textViewUsername = requireView().findViewById(R.id.textViewUsername)
        editTextUsername = requireView().findViewById(R.id.editTextUsername)
        editTextEmail = requireView().findViewById(R.id.editTextEmail)
        editTextPassword = requireView().findViewById(R.id.editTextPassword)

        divider = requireView().findViewById(R.id.divider)

        SigninBtn = requireView().findViewById(R.id.SigninBtn)
        CreateAccountBtn = requireView().findViewById(R.id.CreateAccountBtn)
        RegisterBtn = requireView().findViewById(R.id.RegisterBtn)
        LogoutBtn = requireView().findViewById(R.id.LogoutBtn)
        BackBtn = requireView().findViewById(R.id.BackBtn)

        textViewMessage = requireView().findViewById(R.id.textViewMessage)

        editTextPassword?.setText("")
        textViewMessage?.text = ""

        if (auth.currentUser != null && !auth.currentUser!!.isAnonymous)
        {
            Log.d("log", "not anonymous")
            textViewUsername?.visibility = View.VISIBLE
            editTextUsername?.visibility = View.GONE
            editTextEmail?.visibility = View.GONE
            editTextPassword?.visibility = View.GONE
            divider?.visibility = View.VISIBLE
            SigninBtn?.visibility = View.GONE
            CreateAccountBtn?.visibility = View.GONE
            RegisterBtn?.visibility = View.GONE
            LogoutBtn?.visibility = View.VISIBLE
            BackBtn?.visibility = View.GONE

            var usrname: String = "User"
            try {
                ctx.openFileInput("username.txt").bufferedReader().use { inputStream ->
                    usrname = inputStream.readText()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            textViewUsername?.text = "Signed In As\n" + usrname
        }
        else
        {
            Log.d("log", "not signed in")
            textViewUsername?.visibility = View.GONE
            editTextUsername?.visibility = View.GONE
            editTextEmail?.visibility = View.VISIBLE
            editTextPassword?.visibility = View.VISIBLE
            divider?.visibility = View.VISIBLE
            SigninBtn?.visibility = View.VISIBLE
            CreateAccountBtn?.visibility = View.GONE
            RegisterBtn?.visibility = View.VISIBLE
            LogoutBtn?.visibility = View.GONE
            BackBtn?.visibility = View.GONE
        }

        /*if (connected != "")
        {

        }
        else
        {
            Log.d("log", "no connection")
            textViewUsername?.visibility = View.VISIBLE
            editTextUsername?.visibility = View.GONE
            editTextEmail?.visibility = View.GONE
            editTextPassword?.visibility = View.GONE
            divider?.visibility = View.GONE
            SigninBtn?.visibility = View.GONE
            CreateAccountBtn?.visibility = View.GONE
            RegisterBtn?.visibility = View.GONE
            LogoutBtn?.visibility = View.GONE

            textViewUsername?.text = "Connecting to Server"
        }*/

        SigninBtn!!.setOnClickListener {
            textViewMessage?.text = ""
            Log.d("log", editTextUsername?.text.toString() + " " + editTextPassword?.text.toString())
            if (editTextEmail?.text.toString() != "" && editTextPassword?.text.toString() != "")
            {
                Log.d("log", "xx")
                SigninBtn?.isEnabled = false
                RegisterBtn?.isEnabled = false
                textViewUsername?.text = ""
                auth.signInWithEmailAndPassword(editTextEmail?.text.toString(), editTextPassword?.text.toString()).addOnSuccessListener {
                    Log.d("log", "signed in")
                    var name: String = "User"
                    val docRef = db.collection("data").document(auth.currentUser!!.uid)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d("log", "DocumentSnapshot data: ${document.data}")
                                for (filename in fileNameList) {
                                    try {
                                        ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                                            it.write(document.data?.get(filename.split(".")[0]).toString().toByteArray())
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                textViewUsername?.text = "Signed In As\n" + document.data?.get("username").toString()
                            } else {
                                Log.d("log", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("log", "get failed with ", exception)
                        }
                    textViewUsername?.visibility = View.VISIBLE
                    editTextUsername?.visibility = View.GONE
                    editTextEmail?.visibility = View.GONE
                    editTextPassword?.visibility = View.GONE
                    divider?.visibility = View.VISIBLE
                    SigninBtn?.visibility = View.GONE
                    CreateAccountBtn?.visibility = View.GONE
                    RegisterBtn?.visibility = View.GONE
                    LogoutBtn?.visibility = View.VISIBLE
                    BackBtn?.visibility = View.GONE

                }. addOnFailureListener { exception ->
                    SigninBtn?.isEnabled = true
                    RegisterBtn?.isEnabled = true

                    textViewUsername?.visibility = View.GONE
                    editTextUsername?.visibility = View.GONE
                    editTextEmail?.visibility = View.VISIBLE
                    editTextPassword?.visibility = View.VISIBLE
                    divider?.visibility = View.VISIBLE
                    SigninBtn?.visibility = View.VISIBLE
                    CreateAccountBtn?.visibility = View.GONE
                    RegisterBtn?.visibility = View.VISIBLE
                    LogoutBtn?.visibility = View.GONE
                    BackBtn?.visibility = View.GONE

                    editTextPassword?.setText("")
                    textViewMessage?.text = exception.toString().substringAfter("Exception: ")
                }
            }
            else
            {
                textViewMessage?.text = "Please check sign-in info"
            }
        }

        LogoutBtn!!.setOnClickListener {
            textViewMessage?.text = ""
            LogoutBtn!!.isEnabled = false
            auth.signOut()
            for (filename in fileNameList) {
                try {
                    ctx.openFileOutput(filename, Context.MODE_PRIVATE).use {
                        it.write("".toByteArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            LogoutBtn!!.isEnabled = true
            SigninBtn!!.isEnabled = true
            RegisterBtn!!.isEnabled = true

            editTextEmail?.setText("")
            editTextPassword?.setText("")

            textViewUsername?.visibility = View.GONE
            editTextUsername?.visibility = View.GONE
            editTextEmail?.visibility = View.VISIBLE
            editTextPassword?.visibility = View.VISIBLE
            divider?.visibility = View.VISIBLE
            SigninBtn?.visibility = View.VISIBLE
            CreateAccountBtn?.visibility = View.GONE
            RegisterBtn?.visibility = View.VISIBLE
            LogoutBtn?.visibility = View.GONE
            BackBtn?.visibility = View.GONE
        }

        RegisterBtn!!.setOnClickListener {
            textViewUsername?.visibility = View.GONE
            editTextUsername?.visibility = View.VISIBLE
            editTextEmail?.visibility = View.VISIBLE
            editTextPassword?.visibility = View.VISIBLE
            divider?.visibility = View.GONE
            SigninBtn?.visibility = View.GONE
            CreateAccountBtn?.visibility = View.VISIBLE
            RegisterBtn?.visibility = View.GONE
            LogoutBtn?.visibility = View.GONE
            BackBtn?.visibility = View.VISIBLE

            editTextUsername?.setText("")
            textViewMessage?.text = ""
        }

        BackBtn!!.setOnClickListener {
            textViewUsername?.visibility = View.GONE
            editTextUsername?.visibility = View.GONE
            editTextEmail?.visibility = View.VISIBLE
            editTextPassword?.visibility = View.VISIBLE
            divider?.visibility = View.VISIBLE
            SigninBtn?.visibility = View.VISIBLE
            CreateAccountBtn?.visibility = View.GONE
            RegisterBtn?.visibility = View.VISIBLE
            LogoutBtn?.visibility = View.GONE
            BackBtn?.visibility = View.GONE
        }

        CreateAccountBtn!!.setOnClickListener {
            CreateAccountBtn?.isEnabled = false
            textViewMessage?.text = ""
            if (editTextEmail?.text.toString() != "" && editTextPassword?.text.toString() != "" && editTextUsername?.text.toString() != "")
            {
                auth.createUserWithEmailAndPassword(editTextEmail?.text.toString(), editTextPassword?.text.toString()).addOnSuccessListener {
                    val usr = auth.currentUser
                    if (usr!=null)
                    {
                        val docRef = db.collection("data").document(usr.uid)
                        val localData: HashMap<String, String> = hashMapOf()
                        for (filename in fileNameList) {
                            try {
                                ctx.openFileInput(filename).bufferedReader().use { inputStream ->
                                    localData[filename.split(".")[0]] = inputStream.readText()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                localData[filename.split(".")[0]] = ""
                            }
                        }
                        try {
                            ctx.openFileOutput("username.txt", Context.MODE_PRIVATE).use {
                                it.write(editTextUsername?.text.toString().toByteArray())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        localData["username"] = editTextUsername?.text.toString()
                        docRef.set(localData).addOnSuccessListener {
                            Log.d("log", "uploadData:success")
                        }.addOnFailureListener {
                            Log.d("log", "uploadData:fail")
                        }
                    }
                    CreateAccountBtn?.isEnabled = true

                    textViewUsername?.visibility = View.VISIBLE
                    editTextUsername?.visibility = View.GONE
                    editTextEmail?.visibility = View.GONE
                    editTextPassword?.visibility = View.GONE
                    divider?.visibility = View.VISIBLE
                    SigninBtn?.visibility = View.GONE
                    CreateAccountBtn?.visibility = View.GONE
                    RegisterBtn?.visibility = View.GONE
                    LogoutBtn?.visibility = View.VISIBLE
                    BackBtn?.visibility = View.GONE

                    textViewUsername?.text = "Signed In\n" + editTextUsername?.text.toString()
                }.addOnFailureListener { exception ->
                    CreateAccountBtn?.isEnabled = true
                    textViewMessage?.text = exception.toString().substringAfter("Exception: ")

                }
            }
            else
            {
                textViewMessage?.text = "Please check account info"
            }
            CreateAccountBtn?.isEnabled = true
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Account.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}