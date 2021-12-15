package com.example.smiline.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.smiline.R
import com.example.smiline.databinding.FragmentHomeBinding
import com.example.smiline.model.db.AppDatabase
import com.example.smiline.model.db.Course
import com.example.smiline.util.ui.listAdapter.ListAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private var courses : List<Course>? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        GlobalScope.launch {
            val db: AppDatabase = Room
                .databaseBuilder(
                    requireContext().applicationContext,
                    AppDatabase::class.java, "database-name"
                ).fallbackToDestructiveMigration()
                .build()
            courses = db.userDao().getAll()
            courses.let {
                val list_view = root.findViewById<ListView>(R.id.list_view)
                if(courses != null) {
                    val adapter = ListAdapter(requireContext(), courses!!)
                    println("adapter"+adapter.toString())
                    activity?.runOnUiThread {
                        list_view.adapter = adapter
                    }
                }
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}