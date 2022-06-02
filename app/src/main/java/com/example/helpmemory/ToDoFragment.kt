package com.example.helpmemory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
    // 선택된 날짜
    private var selectedDate: LocalDate? = null
    // 오늘 날짜
    private val today = LocalDate.now()
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val todos = mutableMapOf<LocalDate, List<ToDo>>()
    // todo 클릭 시, 삭제 가능
    private val todoAdapter = ToDoAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteTodo(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    // todo 추가하는 dialog
    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            val padding = dpToPx(20, requireContext())
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.input_dialog_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveTodo(editText.text.toString())
                editText.setText("")
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnShowListener {
                    // 키보드 보이기
                    editText.requestFocus()
                    context.inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                }
                setOnDismissListener {
                    // 키보드 숨기기
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
        // 날짜 위에 요일 표시하는 부분
        val daysOfWeek = daysOfWeekFromLocale()
        binding.weekLayout.root.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase(Locale.ENGLISH)
                setTextColorRes(R.color.white_light)
            }
        }
        // 현재 보이는 달력의 월
        val currentMonth = YearMonth.now()
        binding.calendarView.apply {
            // 달력 최초 setup
            setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            binding.calendarView.post {
                // 초기 날짜를 오늘로 설정
                selectDate(today)
            }
        }

        // Calendar 컨테이너
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
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
                // 캘린더의 일수
                val textView = container.binding.dayText
                // ToDo가 있다면 dotView 가 보임
                val dotView = container.binding.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        // 오늘 날짜
                        today -> {
                            textView.setTextColorRes(R.color.white)
                            textView.setBackgroundResource(R.drawable.today_background)
                            dotView.makeInVisible()
                        }
                        // 선택된 날짜
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

        // 달력 좌우 스크롤 시, 발생하는 동작
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

            selectDate(it.yearMonth.atDay(1))
        }

        binding.addButton.setOnClickListener {
            Log.d("btnSetup", "Selected")
            inputDialog.show()
        }
    }

    // 날짜 선택 함수
    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    // update 함수
    private fun updateAdapterForDate(date: LocalDate) {
        todoAdapter.apply {
            todos.clear()
            todos.addAll(this@ToDoFragment.todos[date].orEmpty())
            notifyDataSetChanged()
        }
        binding.selectedDateText.text = selectionFormatter.format(date)
    }

    // todo 추가 함수
    private fun saveTodo(text: String) {
        // editText 가 비어있다면, toast 발생
        if (text.isBlank()) {
            Toast.makeText(requireContext(), R.string.empty_input_text, Toast.LENGTH_LONG).show()
        }
        // todos에 todo 추가
        else {
            selectedDate?.let {
                todos[it] = todos[it].orEmpty().plus(ToDo(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
            }
        }
    }

    // todo 삭제 함수
    private fun deleteTodo(todo: ToDo) {
        val date = todo.date
        todos[date] = todos[date].orEmpty().minus(todo)
        updateAdapterForDate(date)
    }
}