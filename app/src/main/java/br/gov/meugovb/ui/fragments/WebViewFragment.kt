package br.gov.meugovb.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.gov.meugovb.R
import br.gov.meugovb.databinding.WebViewFragmentBinding
import br.gov.meugovb.ui.activities.dataStore
import br.gov.meugovb.viewmodel.LeprechaunVM
import br.gov.meugovb.viewmodel.LeprechaunVMFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebViewFragment : Fragment() {
    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding!!
    lateinit var webView: WebView
    lateinit var myVM: LeprechaunVM
    private var messageAb: ValueCallback<Array<Uri?>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WebViewFragmentBinding.inflate(inflater, container, false)

        val vmFactory = LeprechaunVMFactory(this@WebViewFragment.activity!!.application)
        myVM = ViewModelProvider(this, vmFactory)[LeprechaunVM::class.java]

        webView = binding.webView
        arguments?.getString(ARGUMENTS_NAME)?.let { webView.loadUrl(it) }
        webView.webViewClient = LocalClient()
        webView.settings.userAgentString = USER_AGENT
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback
                selectImageIfNeed()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(requireContext())
                newWebView.webChromeClient = this
                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            messageAb?.onReceiveValue(null)
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (messageAb == null) return

            messageAb!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            messageAb = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        isEnabled = false
                    }
                }
            })
    }

    private fun selectImageIfNeed() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = INTENT_TYPE
        startActivityForResult(
            Intent.createChooser(i, CHOOSER_TITLE),
            RESULT_CODE
        )
    }

    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            CookieManager.getInstance().flush()
            url?.let { url ->
                if (url == BASE_URL) {
                    findNavController().navigate(R.id.gameFragment)
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (myVM.checkDataStoreValue(
                                SHARED_PREF_NAME,
                                this@WebViewFragment.requireContext()
                            ).isNullOrEmpty()
                        ) {
                            val dataStoreKey = stringPreferencesKey(SHARED_PREF_NAME)
                            context!!.dataStore.edit { sharedPref ->
                                sharedPref[dataStoreKey] = "final"
                            }
                            myVM.saveUrl(url, context!!)
                        }
                    }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARGUMENTS_NAME = "url"
        const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
        const val INTENT_TYPE = "image/*"
        const val CHOOSER_TITLE = "Image Chooser"
        const val BASE_URL = "https://buffalorides.online/"
        const val SHARED_PREF_NAME = "sharedPref"
        const val RESULT_CODE = 1
    }
}