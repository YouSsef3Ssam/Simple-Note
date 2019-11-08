package com.example.simplenote.view.notes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.simplenote.utils.Constants;
import com.example.simplenote.utils.Paginator;
import com.example.simplenote.local.models.Note;
import com.example.simplenote.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.simplenote.view.signIn.SignInFragment.userId;

public class NotesFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private NotesViewModel notesViewModel;

    //Views
    private RecyclerView recyclerView;
    private TextView toolbarTV;
    private ImageView removeAllNotes;
    private ImageView addNote;
    private View noResultsView;
    private View loadingView;
    private NestedScrollView nestedScrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView endResultsTextView;
    private ProgressBar loadMoreProgressBar;
    private LinearLayoutManager linearLayoutManager;


    private Bundle bundle;
    private Paginator paginator;
    private NoteAdapter noteAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        toolbarTV.setText(getContext().getString(R.string.my_notes));

        initRV();
        noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);


        bundle = this.getArguments();

        notesViewModel = ViewModelProviders.of(this).get(NotesViewModel.class);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note note = noteAdapter.getNote(viewHolder.getAdapterPosition());
                noteAdapter.reset();
                notesViewModel.delete(note);
                notesViewModel.deleteWithFirebase(note, userId);
                Toast.makeText(getContext(), getContext().getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.notesFragment);
            }
        }).attachToRecyclerView(recyclerView);

        noteAdapter.setOnNoteClickListner(note -> {
            bundle = new Bundle();
            bundle.putString(Constants.EXTRA_TITLE, note.getTitle());
            bundle.putString(Constants.EXTRA_DESCRIPTION, note.getDescription());
            bundle.putInt(Constants.EXTRA_ID, note.getId());
            bundle.putString(Constants.TYPE, getContext().getString(R.string.edit));
            bundle.putString(Constants.USER_ID, userId);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addEditNoteFragment, bundle);
        });

        removeAllNotes.setOnClickListener(this);
        addNote.setOnClickListener(this);

        onNestedScrollChange();
        getNotes();
    }

    private void initViews(View view) {
        removeAllNotes = view.findViewById(R.id.deleteAllNotes);
        toolbarTV = view.findViewById(R.id.toolbar_textView);
        addNote = view.findViewById(R.id.addNote);
        recyclerView = view.findViewById(R.id.notesRecyclerView);
        loadMoreProgressBar = view.findViewById(R.id.loadMore_progressBar);
        noResultsView = view.findViewById(R.id.no_results_found_view);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        endResultsTextView = view.findViewById(R.id.end_results_textView);
        loadingView = view.findViewById(R.id.loading_view);
        loadingView.setVisibility(View.VISIBLE);

    }

    private void initRV() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        paginator = new Paginator(Constants.PAGE_NUMBER, Constants.PAGE_SIZE);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void onNestedScrollChange() {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > oldScrollY) {
                        paginator.performChanged(linearLayoutManager);
                    }
                    if (scrollY < oldScrollY) {
                        paginator.performChanged(linearLayoutManager);
                    }
                    if (scrollY == 0) {

                    }
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        loadMoreProgressBar.setVisibility(View.VISIBLE);
                        if (paginator.loadMore()) {
                            loadMoreProgressBar.setVisibility(View.GONE);
                            getNotes();
                        }
                        if (!paginator.isExtraData()) {
                            loadMoreProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void getNotes() {
        notesViewModel.getNotes(paginator.getPageNumber() * paginator.getItemsCount(), paginator.getItemsCount()).observe(this, notes -> {
            if (notes.isEmpty() && paginator.getPageNumber() == 0) {
                getDataFromFirebase();
                noteAdapter.setNotes(notes);
                noResultsView.setVisibility(View.VISIBLE);
                endResultsTextView.setVisibility(View.GONE);
            } else {
                noResultsView.setVisibility(View.GONE);
                noteAdapter.setNotes(notes);
                if (notes.size() < Constants.PAGE_SIZE) {
                    paginator.setExtraData(false);
                    endResultsTextView.setVisibility(View.VISIBLE);
                }
            }
        });
        loadingView.setVisibility(View.GONE);
        loadMoreProgressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getDataFromFirebase() {
        sync();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        reference.child(getContext().getString(R.string.notes)).child(userId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(getContext(), getContext().getString(R.string.no_online_data), Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    notesViewModel.insertNotesFromFirebase(note);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sync() {
        if (isOnline()) {
            Toast.makeText(getContext(), getContext().getString(R.string.syncing), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getContext().getString(R.string.syncing_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addNote:
                paginator.setPageNumber(0);
                paginator.setPageNumber(0);
                bundle = new Bundle();
                bundle.putString(Constants.TYPE, getContext().getString(R.string.add));
                bundle.putString(Constants.USER_ID, userId);
                Navigation.findNavController(view).navigate(R.id.addEditNoteFragment, bundle);
                break;
            case R.id.deleteAllNotes:
                noteAdapter.reset();
                Navigation.findNavController(view).navigate(R.id.notesFragment);
                notesViewModel.deleteAll();
                notesViewModel.deleteAllWithFirebase(userId);
                Toast.makeText(getContext(), getContext().getString(R.string.all_note_deleted), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onRefresh() {
        initialPagination();
    }

    private void initialPagination() {
        noteAdapter.reset();
        endResultsTextView.setVisibility(View.GONE);
        paginator = new Paginator(Constants.PAGE_NUMBER, Constants.PAGE_SIZE);
        swipeRefreshLayout.post(() -> getNotes());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.notesFragment);
    }
}