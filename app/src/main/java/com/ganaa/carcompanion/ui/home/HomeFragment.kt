package com.ganaa.carcompanion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ganaa.carcompanion.databinding.FragmentHomeBinding
import com.ganaa.carcompanion.R
import com.ganaa.carcompanion.VehicleData
import android.util.Log

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try{
            setupObservers()

        } catch (e: Exception) {
            Log.e("HomeFragment", "Error in onViewCreated: ${e.message}", e)
        }
    }

    private fun setupObservers() {
        homeViewModel.vehicleData.observe(viewLifecycleOwner) { data ->
            try {
                updateUI(data)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error updating UI: ${e.message}", e)
            }
        }
    
        homeViewModel.connectionStatus.observe(viewLifecycleOwner) { isConnected ->
            try {
                updateConnectionStatus(isConnected)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error updating connection status: ${e.message}", e)
            }
        }
    }
    
    
    private fun updateConnectionStatus(isConnected: Boolean) {
        binding.connectionStatus.apply {
            when (isConnected) {
                true -> {
                    text = getString(R.string.connected)
                    setTextColor(resources.getColor(android.R.color.holo_green_light, null))
                }
                false, null -> {
                    text = getString(R.string.not_connected)
                    setTextColor(resources.getColor(android.R.color.holo_red_light, null))
                }
            }
        }
    }

    private fun updateUI(data: VehicleData) {
        // Update RPM gauge
        binding.rpmGauge.setRpm(data.rpm)
        
        // Update speed gauge
        binding.speedGauge.setSpeed(data.speed)
        
        // Update engine temperature
        binding.engineTempValue.text = "${data.engineTemp} °C"
        
        // Update MAP sensor
        binding.mapSensorValue.text = "${data.mapSensor} kPa"
        
        // Update tire pressure values
        binding.tirePressureFl.text = "${data.tirePressure.frontLeft} PSI"
        binding.tirePressureFr.text = "${data.tirePressure.frontRight} PSI"
        binding.tirePressureRl.text = "${data.tirePressure.rearLeft} PSI"
        binding.tirePressureRr.text = "${data.tirePressure.rearRight} PSI"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}