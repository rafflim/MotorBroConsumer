package com.elevintech.motorbro.Dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DashboardBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_dialog_dashboard, container, false)
    }
}
