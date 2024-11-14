package com.example.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTitle.setText(task.getTitle());
        holder.textViewDescription.setText(task.getDescription());

        // Set listener untuk tombol edit
        holder.buttonEdit.setOnClickListener(v -> editTask(position));

        // Set listener untuk tombol delete
        holder.buttonDelete.setOnClickListener(v -> deleteTask(position));
    }

    private void editTask(int position) {
        Task task = taskList.get(position);

        // Inflater layout dialog untuk edit
        View editView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_task, null);
        EditText editTextTitle = editView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = editView.findViewById(R.id.editTextDescription);

        // Set current task data ke EditText
        editTextTitle.setText(task.getTitle());
        editTextDescription.setText(task.getDescription());

        // Tampilkan dialog edit
        new AlertDialog.Builder(context)
                .setTitle("Edit Task")
                .setView(editView)
                .setPositiveButton("Save", (dialog, which) -> {
                    task.setTitle(editTextTitle.getText().toString());
                    task.setDescription(editTextDescription.getText().toString());
                    notifyItemChanged(position); // Update tampilan item
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDescription;
        Button buttonEdit, buttonDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}