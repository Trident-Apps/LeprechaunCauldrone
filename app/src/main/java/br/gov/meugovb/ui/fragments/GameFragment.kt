package br.gov.meugovb.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.gov.meugovb.R
import br.gov.meugovb.databinding.GameFragmentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment() {
    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding!!
    private var points = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(
            binding.imageView,
            binding.imageView2,
            binding.imageView3,
            binding.imageView4,
            binding.imageView5
        ).forEach { imageView ->
            imageView.setOnClickListener { view ->
                onClick(view as ImageView)
            }
        }
        play()

    }

    private fun play() {
        lifecycleScope.launch {
            repeat(20) {
                delay(800)
                shuffle()
                if (points >= 5) {
                    points = 0
                    findNavController().navigate(R.id.victoryFragment)
                }
            }
        }
    }

    private fun onClick(view: ImageView) {
        if (view.tag == R.drawable.ic5) {
            view.isClickable = false
            points++
            binding.points.text = "${points}/5"
        }
    }

    private fun shuffle() {
        val imList = listOf(
            R.drawable.ic1,
            R.drawable.ic2,
            R.drawable.ic3,
            R.drawable.ic4,
            R.drawable.ic5
        ).shuffled()
        binding.imageView.setImageResource(imList[0])
        binding.imageView.tag = imList[0]
        binding.imageView2.setImageResource(imList[1])
        binding.imageView2.tag = imList[1]
        binding.imageView3.setImageResource(imList[2])
        binding.imageView3.tag = imList[2]
        binding.imageView4.setImageResource(imList[3])
        binding.imageView4.tag = imList[3]
        binding.imageView5.setImageResource(imList[4])
        binding.imageView5.tag = imList[4]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
