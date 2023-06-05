package com.lrm.unscramble.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lrm.unscramble.R
import com.lrm.unscramble.data.GameViewModel
import com.lrm.unscramble.data.MAX_NO_OF_WORDS
import com.lrm.unscramble.databinding.FragmentGameBinding


class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        Log.i("GameFragment", "GameFragment created/ recreated")

        Log.i(
            "GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                    "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}"
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel

        binding.maxNoOfWords = MAX_NO_OF_WORDS

        binding.lifecycleOwner = viewLifecycleOwner

        binding.submit.setOnClickListener {
            if (binding.answer.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Please enter your answer", Toast.LENGTH_SHORT)
                    .show()
            } else {
                onSubmitWord()
            }
        }
        binding.skip.setOnClickListener { onSkipWord() }

        /*viewModel.currentWordCount.observe(viewLifecycleOwner) { newWordCount ->
            binding.questionCount.text =
                getString(R.string.question_count, newWordCount, MAX_NO_OF_WORDS)
        }

        viewModel.score.observe(viewLifecycleOwner) { newScore ->
            binding.tvScore.text = getString(R.string.score, newScore)
        }

        viewModel.currentScrambledWord.observe(viewLifecycleOwner) { newWord ->
            binding.question.text = newWord
        }*/
    }

    private fun onSubmitWord() {
        val playerWord = binding.answer.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.questionCount()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    private fun onSkipWord() {
        if (viewModel.questionCount()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.score, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    private fun exitGame() {
        activity?.finish()
    }

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.answerTil.isErrorEnabled = true
            binding.answerTil.error = getString(R.string.try_again)
        } else {
            binding.answerTil.isErrorEnabled = false
            binding.answer.text = null
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("GameFragment", "GameFragment destroyed")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}