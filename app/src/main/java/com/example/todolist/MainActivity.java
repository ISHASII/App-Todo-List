package com.example.todolist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextTask;
    private EditText editTextDescription;
    private Button buttonAddTask;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan komponen UI dengan ID
        editTextTask = findViewById(R.id.editTextTask);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        // Inisialisasi daftar task dan adapter
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        // Tombol untuk menambahkan task baru
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTask.getText().toString();
                String description = editTextDescription.getText().toString();
                if (!title.isEmpty()) {
                    Task task = new Task(title, description);
                    taskList.add(task);
                    taskAdapter.notifyDataSetChanged();
                    showTimePickerDialog(title, description); // Menampilkan dialog waktu dengan data task
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Menampilkan TimePickerDialog untuk memilih waktu alarm
    private void showTimePickerDialog(String title, String description) {
        try {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                setAlarm(calendar, title, description); // Memanggil fungsi setAlarm dengan data task
            }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
            timePickerDialog.show();
        } catch (Exception e) {
            Log.e("TimePickerDialog", "Error showing time picker dialog", e);
            Toast.makeText(this, "An error occurred while setting the time", Toast.LENGTH_SHORT).show();
        }
    }

    // Menyetel alarm menggunakan AlarmManager
    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(Calendar calendar, String title, String description) {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, ReminderBroadcast.class);
            // Mengirim judul dan deskripsi task ke ReminderBroadcast
            intent.putExtra("task_title", title);
            intent.putExtra("task_description", description);

            // Menggunakan ID unik berdasarkan title.hashCode()
            int pendingIntentId = title.hashCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, "Alarm set for: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
            } else {
                throw new Exception("AlarmManager is null");
            }
        } catch (Exception e) {
            Log.e("AlarmManager", "Error setting alarm", e);
            Toast.makeText(this, "An error occurred while setting the alarm", Toast.LENGTH_SHORT).show();
        }
    }
}
