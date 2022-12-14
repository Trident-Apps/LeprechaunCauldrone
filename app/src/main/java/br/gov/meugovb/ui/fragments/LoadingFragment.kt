package br.gov.meugovb.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.gov.meugovb.R
import br.gov.meugovb.utils.Checkers
import br.gov.meugovb.viewmodel.LeprechaunVM
import br.gov.meugovb.viewmodel.LeprechaunVMFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingFragment : Fragment(R.layout.laoding_fragment) {
    private val checker = Checkers()
    private lateinit var myVM: LeprechaunVM


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = LeprechaunVMFactory(this@LoadingFragment.activity!!.application)
        myVM = ViewModelProvider(this, vmFactory)[LeprechaunVM::class.java]

        if (!checker.isDeviceSecured(this@LoadingFragment.requireActivity())) {

            lifecycleScope.launch(Dispatchers.IO) {
                val dataStore = myVM.checkDataStoreValue(DATASTORE_KEY, context!!)

                if (dataStore == null) {
                    myVM.getDeepLink(this@LoadingFragment.requireActivity())

                    lifecycleScope.launch(Dispatchers.Main) {
                        myVM.urlLiveData.observe(viewLifecycleOwner) {
                            startWebView(it)
                        }
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        startWebView(dataStore.toString())
                    }
                }
            }
        } else {
            startGame()
        }
    }

    private fun startGame() {
        findNavController().navigate(R.id.gameFragment)
    }

    private fun startWebView(url: String) {
        val bundle = bundleOf(ARGUMENTS_NAME to url)
        findNavController().navigate(R.id.webViewFragment, bundle)
    }

    companion object {
        const val ARGUMENTS_NAME = "url"
        const val DATASTORE_KEY = "finalUrl"
    }
}