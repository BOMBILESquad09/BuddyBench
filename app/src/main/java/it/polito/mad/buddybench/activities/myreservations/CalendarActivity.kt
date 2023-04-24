package it.polito.mad.buddybench.activities.myreservations


/*
@AndroidEntryPoint
class CalendarActivity : AppCompatActivity() {



    var  selectedDate: LocalDate? = null
    val reservations: MutableLiveData<HashMap<LocalDate,List<ReservationDTO>>> = MutableLiveData(
        null
    )

    private val bottomBar = BottomBar(this)
    lateinit var recyclerViewReservations: RecyclerView

    private val viewModel by viewModels<ReservationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Call the repo in this way
        Log.d("HiltApplication", application.javaClass.name)

        // Adding the ViewModel inside reservations with observer
        viewModel.getAll().observe(this, androidx.lifecycle.Observer {
            reservations.value = it
        })

        setContentView(R.layout.my_reservations)
        val calendarView = findViewById<CalendarView>(R.id.reservations)
        recyclerViewReservations = findViewById(R.id.reservations)
        recyclerViewReservations.layoutManager = LinearLayoutManager(this)
        val dayTitle = findViewById<TextView>(R.id.dayTitle)
        dayTitle.text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View): DayViewContainer {

                return DayViewContainer(view)
            }
            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.setOnClickCallback{
                    if (container.day.position == DayPosition.MonthDate) {
                        if (selectedDate != container.day.date) {
                            val oldDate = selectedDate
                            selectedDate = container.day.date
                            if(oldDate != null)
                                calendarView.notifyDateChanged(oldDate)
                            calendarView.notifyDateChanged(data.date)
                        }
                    }
                    dayTitle.text = selectedDate?.format(DateTimeFormatter.ofPattern("EEEE, d MMMM y"))
                }
                container.textView.text = data.date.dayOfMonth.toString()
                container.setBackground(selectedDate )
                container.setTextColor(selectedDate, this@CalendarActivity)
                container.reservations = reservations.value?.get(data.date)
                container.setSportsIcon(this@CalendarActivity)

                if(selectedDate == null) {
                    recyclerViewReservations.adapter = ReservationAdapter(
                        reservations.value?.get(LocalDate.now()) ?: listOf())
                } else {
                    recyclerViewReservations.adapter = ReservationAdapter(
                        reservations.value?.get(selectedDate) ?: listOf())
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed
        val daysOfWeek = daysOfWeek(DayOfWeek.MONDAY)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first()) // Available from the library
        calendarView.scrollToMonth(currentMonth)
        val monthName = findViewById<TextView>(R.id.monthName)

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth){
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    (container.titlesContainer.children.first() as ViewGroup).children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek[index]
                            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                        }
                }
            }
        }

        val previousButton = findViewById<ImageView>(R.id.previousButton)
        val nextButton = findViewById<ImageView>(R.id.nextButton)
        calendarView.monthScrollListener = { month ->
            monthName.text = month.yearMonth.displayText()
        }
        previousButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        nextButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)

            }
        }

        bottomBar.setup()
    }
}


*/