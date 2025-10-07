package com.example.cos20031

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import kotlin.io.path.name

class BottomFragment : Fragment() {

    private lateinit var viewModel: NameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullNameTextView: TextView = view.findViewById(R.id.fullname)
        viewModel = ViewModelProvider(requireActivity()).get(NameViewModel::class.java)

        viewModel.name.observe(viewLifecycleOwner) { newName ->
            fullNameTextView.text = newName
        }
    }
}
