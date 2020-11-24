package com.foxdev.calculator.ui.calculator

import android.content.ClipData
import android.content.ClipboardManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.foxdev.calculator.services.MessageListener
import net.objecthunter.exp4j.ExpressionBuilder

// ViewModel class is part of MVVM pattern
class CalculatorViewModel() : ViewModel() {

    // fields declaration
    private val _correctEquationPattern: Regex = Regex("""^((\d|\d\.\d)+[+/\-*.])+(\d|\d\.\d)+$""")
    private val _equation = MutableLiveData<String>().apply {value = ""}
    private val _isEquationWrong = MutableLiveData<Boolean>().apply {value = false}
    private lateinit var _messageListener: MessageListener
    private lateinit var _clipboard: ClipboardManager

    // public fields that will send notification on value change
    val equation: LiveData<String>
        get() = _equation
    val isEquationWrong: LiveData<Boolean>
        get() = _isEquationWrong

    // setting layout-connected functionalities
    fun setMessageListener(listener: MessageListener){
        _messageListener = listener
    }
    fun setClipboardManager(manager: ClipboardManager){
        _clipboard = manager
    }

    fun copyEquationToClipboard(){
        if(!checkForCorrectEquation()){
            return
        }
        val data: ClipData = ClipData.newPlainText("equation", _equation.value)
        _clipboard.setPrimaryClip(data)
        _messageListener.sendMessage("Copied equation to clipboard.")
    }

    // --- math functions ---
    fun clearEquation(){
        _equation.value=""
    }
    fun deleteLastCharOfEquation(){
        var eq: String = _equation.value?: "" // assign empty string when _equation.value is null
        if(eq.isNotEmpty()) {
            eq = eq.substring(0, eq.length - 1)
            _equation.value = eq
        }
        checkForCorrectEquation(false)
    }
    fun solveEquation(){
        val eq: String = _equation.value ?: "" // assign empty string when _equation.value is null
        if(!checkForCorrectEquation() || eq.isEmpty()) {
            return
        }
        val exp = ExpressionBuilder(eq).build()
        try {
            val result = exp.evaluate() // do math operation
            _equation.value=result.toString() // assign result of operation
        }catch (ex: ArithmeticException){
            _messageListener.sendMessage(ex.message ?: "Error while evaluating equation.")
        }

    }
    fun addToEquation(char: Char){
        val eq: String = _equation.value ?: "" // assign empty string when _equation.value is null
        val builder: StringBuilder = StringBuilder(eq)
        builder.append(char)
        _equation.value=builder.toString()
        checkForCorrectEquation(false)
    }

    private fun checkForCorrectEquation(showMessage: Boolean = true) : Boolean{
        val eq: String = _equation.value ?: return false // equation is not correct when is null
        _isEquationWrong.value = !_correctEquationPattern.containsMatchIn(eq) // test eq with regex
        if(_isEquationWrong.value!! && showMessage){
            _messageListener.sendMessage("The equation is wrong.")
        }
        return !_isEquationWrong.value!!
    }
}