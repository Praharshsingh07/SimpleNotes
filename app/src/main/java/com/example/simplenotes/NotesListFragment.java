package com.example.simplenotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NotesListFragment extends Fragment {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAddNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        fabAddNote = view.findViewById(R.id.fab_add_note);

        setupRecyclerView();
        setupFab();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Note> notes = dbHelper.getAllNotes();
        adapter = new NoteAdapter(notes, this::onNoteClick);
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        fabAddNote.setOnClickListener(v -> onAddNewNote());
    }

    private void onNoteClick(Note note) {
        NoteDetailFragment detailFragment = NoteDetailFragment.newInstance(note.getId());
        ((MainActivity) requireActivity()).loadFragment(detailFragment, true);
    }

    private void onAddNewNote() {
        NoteDetailFragment detailFragment = NoteDetailFragment.newInstance(0); // 0 indicates a new note
        ((MainActivity) requireActivity()).loadFragment(detailFragment, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotes();
    }

    public void refreshNotes() {
        List<Note> notes = dbHelper.getAllNotes();
        adapter.updateNotes(notes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        fabAddNote = null;
    }
}