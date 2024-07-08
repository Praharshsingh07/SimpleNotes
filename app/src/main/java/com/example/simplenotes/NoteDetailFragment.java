package com.example.simplenotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NoteDetailFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";

    private long noteId;
    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSave;
    private Button buttonDelete;
    private DatabaseHelper dbHelper;

    public static NoteDetailFragment newInstance(long noteId) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noteId = getArguments().getLong(ARG_NOTE_ID);
        }
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextContent = view.findViewById(R.id.edit_text_content);
        buttonSave = view.findViewById(R.id.button_save);
        buttonDelete = view.findViewById(R.id.button_delete);

        buttonSave.setOnClickListener(v -> saveNote());
        buttonDelete.setOnClickListener(v -> deleteNote());

        if (noteId != 0) {
            loadNote();
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        return view;
    }

    private void loadNote() {
        Note note = dbHelper.getNote(noteId);
        if (note != null) {
            editTextTitle.setText(note.getTitle());
            editTextContent.setText(note.getContent());
        }
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (noteId == 0) {
            // Create new note
            Note newNote = new Note(title, content);
            long insertedId = dbHelper.addNote(newNote);
            if (insertedId != -1) {
                Toast.makeText(getContext(), "Note saved", Toast.LENGTH_SHORT).show();
                noteId = insertedId; // Update noteId with the new id
                buttonDelete.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Error saving note", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing note
            Note updatedNote = new Note(noteId, title, content);
            int rowsAffected = dbHelper.updateNote(updatedNote);
            if (rowsAffected > 0) {
                Toast.makeText(getContext(), "Note updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error updating note", Toast.LENGTH_SHORT).show();
            }
        }

        // Refresh the notes list
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).refreshNotesList();
        }

        // Go back to the notes list
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void deleteNote() {
        if (noteId != 0) {
            dbHelper.deleteNote(noteId);
        }
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editTextTitle = null;
        editTextContent = null;
        buttonSave = null;
        buttonDelete = null;
    }
}