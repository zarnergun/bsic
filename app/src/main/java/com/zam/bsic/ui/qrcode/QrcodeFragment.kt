package com.zam.bsic.ui.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zam.bsic.databinding.FragmentHomeBinding
import com.zam.bsic.databinding.FragmentQrcodeBinding
import com.zam.bsic.ui.qrcode.QrcodeViewModel

class QrcodeFragment : Fragment() {

    private var _binding: FragmentQrcodeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val qrcodeViewModel = ViewModelProvider(this)[QrcodeViewModel::class.java]

        _binding = FragmentQrcodeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: FrameLayout = binding.cameraContainer
        return root
    }
}