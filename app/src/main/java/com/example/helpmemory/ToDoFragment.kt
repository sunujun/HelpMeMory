package com.example.helpmemory

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmemory.databinding.CalendarDayLayoutBinding
import com.example.helpmemory.databinding.FragmentToDoBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class ToDoFragment : Fragment() {
    private lateinit var binding: FragmentToDoBinding

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val todos = mutableMapOf<LocalDate, List<ToDo>>()

    private val todoAdapter = ToDoAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20, requireContext())
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.input_dialog_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString())
                // Prepare EditText for reuse.
                editText.setText("")
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    editText.requestFocus()
                    context.inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    context.inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentToDoBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toDoList.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = todoAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }

        val daysOfWeek = daysOfWeekFromLocale()
        binding.weekLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(Locale.ENGLISH)
                setTextColorRes(R.color.white_light)
            }
        }

        val currentMonth = YearMonth.now()

        binding.calendarView.apply {
            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            binding.calendarView.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayLayoutBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.dayText
                val dotView = container.binding.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.today_background)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.blue)
                            textView.setBackgroundResource(R.drawable.selected_background)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.black)
                            textView.background = null
                            dotView.isVisible = todos[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        binding.calendarView.monthScrollListener = {
            if (binding.calendarView.maxRowCount == 6) {
                binding.yearText.text = it.yearMonth.year.toString()
                binding.monthText.text = monthTitleFormatter.format(it.yearMonth)
            } else {
                val firstDate = it.weekDays.first().first().date
                val lastDate = it.weekDays.last().last().date
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    binding.yearText.text = firstDate.yearMonth.year.toString()
                    binding.monthText.text = monthTitleFormatter.format(firstDate)
                } else {
                    binding.monthText.text =
                        "${monthTitleFormatter.format(firstDate)} - ${monthTitleFormatter.format(lastDate)}"
                    if (firstDate.year == lastDate.year) {
                        binding.yearText.text = firstDate.yearMonth.year.toString()
                    } else {
                        binding.yearText.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    }
                }
            }

            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(it.yearMonth.atDay(1))
        }

        binding.addButton.setOnClickListener {
            inputDialog.show()
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        todoAdapter.apply {
            todos.clear()
            todos.addAll(this@ToDoFragment.todos[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.selectedDateText.text = selectionFormatter.format(date)
    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), R.string.empty_input_text, Toast.LENGTH_LONG).show()
        } else {
            selectedDate?.let {
                todos[it] = todos[it].orEmpty().plus(ToDo(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
            }
        }
    }

    private fun deleteEvent(todo: ToDo) {
        val date = todo.date
        todos[date] = todos[date].orEmpty().minus(todo)
        updateAdapterForDate(date)
    }
}