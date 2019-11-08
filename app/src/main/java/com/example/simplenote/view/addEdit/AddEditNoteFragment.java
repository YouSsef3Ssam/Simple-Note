package com.example.simplenote.view.addEdit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.example.simplenote.utils.Constants;
import com.example.simplenote.local.models.Note;
import com.example.simplenote.R;
import static com.example.simplenote.view.signIn.SignInFragment.userId;

public class AddEditNoteFragment extends Fragment implements View.OnClickListener {

    //Note Attribute
    private String title;
    private String description;
    private String type;

    //Views
    private TextView toolbarTV;
    private EditText titleET;
    private EditText descriptionET;
    private ImageView saveNote;
    private ImageView backBtn;

    //AddEditViewModel
    private AddEditViewModel addEditViewModel;

    private Bundle bundle;
    private int id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        addEditViewModel = ViewModelProviders.of(this).get(AddEditViewModel.class);

        bundle = this.getArguments();
        type = bundle.getString(Constants.TYPE);

        if (type.equals(getContext().getString(R.string.edit))) {
            toolbarTV.setText(getString(R.string.edit_note));
            title = bundle.getString(Constants.EXTRA_TITLE);
            description = bundle.getString(Constants.EXTRA_DESCRIPTION);
            id = bundle.getInt(Constants.EXTRA_ID);

            titleET.setText(title);
            descriptionET.setText(description);
        } else {
            toolbarTV.setText(getString(R.string.add_note));
        }
        saveNote.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    private void initViews(View view) {
        toolbarTV = view.findViewById(R.id.toolbar_textView);
        titleET = view.findViewById(R.id.addNoteTitle);
        descriptionET = view.findViewById(R.id.addNoteDescription);
        saveNote = view.findViewById(R.id.saveNote);
        backBtn = view.findViewById(R.id.backBtn);
    }

    private boolean getNoteData() {
        title = titleET.getText().toString();
        description = descriptionET.getText().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getActivity(), getContext().getString(R.string.required_fields), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void save() {
        long currentMillisDate = System.currentTimeMillis();
        Note note = new Note(title, description, currentMillisDate);
        Long noteId = addEditViewModel.insert(note);
        note.setId(noteId.intValue());
        addEditViewModel.insertWithFirebase(note, userId);
        Toast.makeText(getContext(), getContext().getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigate(R.id.notesFragment);
    }

    private void update() {
        long currentMillisDate = System.currentTimeMillis();
        Note note = new Note(title, description, currentMillisDate);
        note.setId(id);
        addEditViewModel.update(note);
        addEditViewModel.updateWithFirebase(note, userId);
        Toast.makeText(getContext(), getContext().getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigate(R.id.notesFragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveNote:
                if (getNoteData()) {
                    if (type.equals(getString(R.string.add)))
                        save();
                    else
                        update();
                }
                break;
            case R.id.backBtn:
                NavHostFragment.findNavController(this).navigate(R.id.notesFragment);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.notesFragment);
    }
}