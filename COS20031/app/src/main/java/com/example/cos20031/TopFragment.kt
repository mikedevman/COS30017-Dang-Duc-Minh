package com.example.cos20031

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class TopFragment : Fragment() {

    private lateinit var viewModel: NameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.top_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(NameViewModel::class.java)

        val firstNameEditText: EditText = view.findViewById(R.id.firstname)
        val lastNameEditText: EditText = view.findViewById(R.id.lastname)
        val okButton: Button = view.findViewById(R.id.button_ok)

        okButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            viewModel.setName("$firstName $lastName")
        }
    }
}
