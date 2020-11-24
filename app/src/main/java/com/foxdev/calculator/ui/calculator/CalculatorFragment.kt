package com.foxdev.calculator.ui.calculator

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.foxdev.calculator.R
import com.foxdev.calculator.databinding.FragmentCalculatorBinding
import com.foxdev.calculator.services.MessageListener

// part of main view that contains calculator topic things
class CalculatorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // --- variables declarations ---
        // ViewModel object controls fragment flow
        val viewModel = CalculatorViewModel()
        // DataBindingUtil object, as a part of MVVM pattern, collects layout informations
        // like root layout and views
        val binding: FragmentCalculatorBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_calculator, container, false)
        val root: View = binding.root

        binding.viewModel = viewModel

        // --- notifications seting ---
        // after this calcEquationText.text property view will be notified when
        // viewModel.equation property changed and will adjust to viewModel.equation value
        viewModel.equation.observe(viewLifecycleOwner, Observer {
            binding.calcEquationText.text = it
        })
        // after this calcEquationText.textColor property view will be notified when
        // viewModel.isEquationWrong property changed and will
        // adjust value using viewModel.isEquationWrong boolean value
        viewModel.isEquationWrong.observe(viewLifecycleOwner, Observer {
            val isWrong = it
            val wrongColor = ContextCompat.getColor(this.requireContext(), R.color.error)
            val correctColor = ContextCompat.getColor(this.requireContext(), R.color.equationText)
            binding.calcEquationText.setTextColor(if(isWrong) wrongColor else correctColor)
        })

        // --- layout-connected functionalities setting ---
        // this set will allow ViewModel to use private field MessageListener's sendMessage method
        // to use layout-connected Toast to show messages.
        viewModel.setMessageListener(object : MessageListener {
            override fun sendMessage(message: String) {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            }
        })
        // this set will allow ViewModel to use private field ClipboardManager
        // to use activity-connected Toast to show messages.
        val clipboard: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        viewModel.setClipboardManager(clipboard)

        return root
    }
}
