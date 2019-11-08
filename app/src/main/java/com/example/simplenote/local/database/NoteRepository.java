package com.example.simplenote.local.database;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.simplenote.local.dao.NoteDao;
import com.example.simplenote.local.models.Note;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteRepository {

    private NoteDao noteDao;

    public NoteRepository(Application application){
        NoteDatabase dataBase = NoteDatabase.getInstance(application);
        noteDao = dataBase.noteDao();
    }

    public Long insert(Note note){
        Long id = null;
        try {
            id = new InsertNoteAsyncTask(noteDao).execute(note).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return id ;
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAll(){
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getNotes(int pageNumber, int pageSize){
        return noteDao.getNotes(pageNumber,  pageSize);
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Long>{
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Long doInBackground(Note... notes) {
            return noteDao.insert(notes[0]);
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }
}