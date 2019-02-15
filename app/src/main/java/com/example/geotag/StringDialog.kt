package com.example.geotag

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

import android.os.Bundle
import android.support.v4.app.DialogFragment

import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText



class StringDialog : DialogFragment() {
    var mOnGotStringListener: GotStringListener?
        get(){
            return mOnGotStringListener
        }
        set(newListener:GotStringListener?){
            mOnGotStringListener = newListener
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)

            // Set up the input
            val input = EditText(it)
            input.inputType = InputType.TYPE_CLASS_TEXT

            builder.setMessage("Save As?")
                .setView(input)
                .setPositiveButton("Save", DialogInterface.OnClickListener { dialog, id ->
                // FIRE ZE MISSILES!
                    mOnGotStringListener ?: mOnGotStringListener!!.onGotString(input.text.toString())
                    this.dismiss()
            })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    this.dismiss()
            })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

abstract class GotStringListener{
    abstract fun onGotString(string : String)
}